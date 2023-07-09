package UserLogic.model

import derevo.circe.codec
import derevo.derive
import derevo.tethys.{tethysReader, tethysWriter}

@derive(codec, tethysReader, tethysWriter)
case class LoginPassword(login: String, password: String)
