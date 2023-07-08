package Auth.model

import derevo.circe.{decoder, encoder}
import derevo.derive

case class UserInfo(id: Long, name: String, surname: String, hash: String, roles: Seq[Role]) {
  def toUser: User = User(id, name, surname, roles)
}

@derive(encoder, decoder)
case class User(id: Long, name: String, surname: String, roles: Seq[Role])
