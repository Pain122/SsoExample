package UserLogic.logic

import Auth.errors.{AuthError, CredentialsError}
import Auth.logic.UserDaoService
import Auth.model.User
import cats.data.EitherT
import cats.effect.kernel.Sync
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effects.SetArg.Ttl
import dev.profunktor.redis4cats.effects.SetArgs
import io.circe.syntax.EncoderOps
import tsec.common.Verified
import tsec.jws.mac.{JWSMacCV, JWTMac}
import tsec.jwt.JWTClaims
import tsec.jwt.algorithms.JWTMacAlgo
import tsec.mac.jca.MacSigningKey
import tsec.passwordhashers.{PasswordHash, PasswordHasher}

import java.time.Instant.now
import scala.concurrent.duration._
import scala.language.postfixOps

trait UserService[F[_]] {
  def authorizeUser(login: String, password: String): EitherT[F, AuthError, String]
}
class UserServiceImpl[F[_]: Sync, A: JWTMacAlgo](
    userService: UserDaoService[F],
    cryptService: PasswordHasher[F, A],
    key: MacSigningKey[A],
    commands: RedisCommands[F, String, User]
)(implicit cv: JWSMacCV[F, A])
    extends UserService[F] {
  override def authorizeUser(login: String, password: String): EitherT[F, AuthError, String] =
    for {
      // Ищем челикса в нашем хранилище пользователей. Это может быть Postgres таблица (login, UserInfo(hashPassword))
      user <- EitherT.fromOptionF(userService.findUser(login), CredentialsError)
      // Делаем проверку, что захэшрованные пароли совпадают
      checked <- EitherT.liftF(
        cryptService.checkpw(password, PasswordHash[A](user.hash))
      )
      // Отбиваемся ошибкой, если что-то идет не так
      _ <-
        if (checked == Verified) EitherT.rightT[F, AuthError](())
        else EitherT.leftT[F, Unit](CredentialsError)
      token <-
        // Рекомендую эту логику вынести в отдельный трейт TokenService
        // Особенно это понадобится, если вы не хотите делать кэширование в Redis,
        // а парсить всю информацию о юзере из самого токена (с двумя функциями - генерация токена, и парсинг UserInfo).
        // В этом случае, на него можно будет делегировать хранение ключа MacSigningKey
        // Редис тут чисто для примера того, как это работает
        EitherT.right(
          JWTMac.build(
            JWTClaims(customFields = Seq("User" -> user.toUser.asJson), expiration = Some(now())),
            key
          )
        )
      // Тут складируем юзера в редис. Когда протухнет запись в редисе, значит токен заэкспайрился
      _ <- EitherT.rightT[F, AuthError](
        commands.set(key = token.toEncodedString, user.toUser, SetArgs(Ttl.Px(1 seconds)))
      )
    } yield token.toEncodedString

  // Для создания пользователя - просто берете с данные с фронта и складируете их в табличку с пользователями.
  // Чтобы захэшировать пароль - вызывайте метод crypt.hashpw
}
