package UserLogic.endpoint

import UserLogic.model.LoginPassword
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.tethysjson.jsonBody

object Endpoints {
  val userLogin: Endpoint[Unit, LoginPassword, Unit, String, Any] =
    endpoint.post
      .in(jsonBody[LoginPassword])
      .out(stringBody)
}
