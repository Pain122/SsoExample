package Auth.logic

import Auth.errors.{AuthError, AuthenticationError}
import Auth.model.{User, UserInfo}
import cats.MonadThrow
import cats.data.EitherT
import cats.implicits.catsSyntaxApplicativeError
import dev.profunktor.redis4cats.RedisCommands

trait Authenticator[F[_]] {
  def authenticate(token: String): EitherT[F, AuthError, User]
}

class AuthenticatorImpl[F[_]: MonadThrow](commands: RedisCommands[F, String, User])
    extends Authenticator[F] {
  override def authenticate(token: String): EitherT[F, AuthError, User] = {
    // Втупую стучимся в редис за данными о пользаке.
    // Если хотите, можете парсить данные из токена с помощью библиотеки hsec
    // https://jmcardon.github.io/tsec/docs/jwt-mac.html
    // Тут логика без рефреш токена, если вы ее не сделаете, я вас не прибью
    // Но как вариант, можно также спарсить JWT токен и постучаться в AuthService, чтобы он нам сгенерировал новый токен
    // В таком случае нужно возвращать пару (User, JwtToken)
    EitherT.fromOptionF(
      commands.get(token).handleErrorWith(_ => MonadThrow[F].pure(None)),
      AuthenticationError
    )
  }
}
