package Auth.logic

import Auth.errors.AuthError
import Auth.model.{Role, UserInfo}
import cats.Applicative
import cats.data.EitherT

trait Authorizer[F[_]] {
  // Тупая проверка ролей. Разрешаю в один трейт с Authenticator объединить
  def authorizeWithRoles(user: UserInfo, role: Role): EitherT[F, AuthError, UserInfo]
}

class AuthorizerImpl[F[_]: Applicative] extends Authorizer[F] {
  override def authorizeWithRoles(user: UserInfo, role: Role): EitherT[F, AuthError, UserInfo] =
    EitherT.apply(Applicative[F].pure(user.roles.contains(role)))
}
