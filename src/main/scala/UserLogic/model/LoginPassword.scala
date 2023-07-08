package UserLogic.model

import derevo.circe.codec
import derevo.derive

@derive(codec)
case class LoginPassword(login: String, password: String)
