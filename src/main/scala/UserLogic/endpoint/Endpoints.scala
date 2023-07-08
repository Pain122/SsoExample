package UserLogic.endpoint

import UserLogic.model.LoginPassword
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import io.circe.generic.auto._

object Endpoints {
  val userLogin: Endpoint[String, Unit, Unit, String, Any] =
    endpoint.post
      .in[[LoginPassword])
      .out(stringBody)
}
