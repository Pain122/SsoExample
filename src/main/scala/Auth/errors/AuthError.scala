package Auth.errors

import Auth.model.Role
import derevo.derive
import derevo.tethys.{tethysReader, tethysWriter}

sealed abstract class AuthError(msg: String) extends Throwable(msg)

case object CredentialsError extends AuthError("Wrong credentials")

@derive(tethysReader, tethysWriter)
case class AuthenticationError() extends AuthError("Authorization failed")

@derive(tethysReader, tethysWriter)
case class AuthorizationError(roles: Seq[Role], required: Seq[Role])
    extends AuthError(s"Insufficient roles. Required: $required, got: $roles")
