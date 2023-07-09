package Auth.logic

import Auth.errors.{AuthError, AuthenticationError, AuthorizationError}
import Auth.model.{Role, User}
import cats.Monad
import cats.implicits.toFunctorOps
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, oneOf, oneOfVariant}
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.jsonWriterMaterializer
import tethys.{JsonReader, JsonWriter}

trait SsoAdder[F[_], SECURITY_INPUT] {
  def add[INPUT, ERROR_OUTPUT: JsonReader: JsonWriter, OUTPUT, R](
      endpoint: Endpoint[SECURITY_INPUT, INPUT, ERROR_OUTPUT, OUTPUT, R],
      serverLogic: User => INPUT => F[Either[ERROR_OUTPUT, OUTPUT]],
      role: Role
  ): ServerEndpoint[R, F]
}

class SsoAdderImpl[F[_]: Monad](authorizer: Authorizer[F], authenticator: Authenticator[F])
    extends SsoAdder[F, String] {
  def add[INPUT, ERROR_OUTPUT: JsonReader: JsonWriter, OUTPUT, R](
      endpoint: Endpoint[String, INPUT, ERROR_OUTPUT, OUTPUT, R],
      serverLogic: User => INPUT => F[Either[ERROR_OUTPUT, OUTPUT]],
      role: Role
  ): ServerEndpoint[R, F] = endpoint
    .errorOutEither[AuthError](
      oneOf[AuthError](
        oneOfVariant(
          StatusCode.Forbidden,
          jsonBody[AuthenticationError].description("Authentication failed")
        ),
        oneOfVariant(
          StatusCode.Unauthorized,
          jsonBody[AuthorizationError].description("Authorization failed")
        )
      )
    )
    .serverSecurityLogic[User, F](token =>
      authenticator
        .authenticate(token)
        .flatMap[AuthError, User](user => authorizer.authorizeWithRoles(user, role))
        .leftMap[Either[ERROR_OUTPUT, AuthError]](err => Right(err))
        .value
    )
    .serverLogic(user =>
      input =>
        serverLogic(user)(input).map {
          case Left(err)  => Left(Left(err))
          case Right(res) => Right(res)
        }
    )

}
