package ServerLogic.endpoints

import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir._

object ExampleEndpoints {
  val SomeLogicEndpoint: Endpoint[String, Unit, Unit, String, Any] =
    endpoint.get
      .securityIn(auth.bearer[String](WWWAuthenticateChallenge.bearer))
      .out(stringBody)

  case class User(name: String)
}
