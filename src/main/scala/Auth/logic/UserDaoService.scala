package Auth.logic

import Auth.model.UserInfo

trait UserDaoService[F[_]] {
  // Где-нибудь в бд сохранить информацию о пользователе
  def findUser(login: String): F[Option[UserInfo]]
}
