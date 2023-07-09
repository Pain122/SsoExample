package Auth.logic

import Auth.errors.AuthError
import Auth.model.{Role, User}
import cats.Applicative
import cats.data.EitherT

trait Authorizer[F[_]] {
  // Тупая проверка ролей. Разрешаю в один трейт с Authenticator объединить
  def authorizeWithRoles(user: User, role: Role): EitherT[F, AuthError, User]
}

class AuthorizerImpl[F[_]: Applicative] extends Authorizer[F] {
  override def authorizeWithRoles(user: User, role: Role): EitherT[F, AuthError, User] =
    EitherT.apply(Applicative[F].pure(user.roles.contains(role)))
}
