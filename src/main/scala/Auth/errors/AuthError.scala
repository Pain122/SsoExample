package Auth.errors

import Auth.model.Role

sealed abstract class AuthError(msg: String) extends Throwable(msg)

case object CredentialsError extends AuthError("Wrong credentials")

case object AuthenticationError extends AuthError("Authorization failed")

case class AuthorizationError(roles: Seq[Role], required: Seq[Role])
    extends AuthError(s"Insufficient roles. Required: $required, got: $roles")
