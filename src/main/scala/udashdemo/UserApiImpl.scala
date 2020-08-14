package udashdemo

import org.joda.time.DateTime

import scala.concurrent.Future

class UserApiImpl extends UserApi {
  def createUser(name: String): Future[User] =
    Future.successful(User(name, createdAt=DateTime.now))

  override def getNow: Future[DateTime] = Future.successful(DateTime.now())

  override def boom: Future[Int] = throw new CodedException("boooooom!!!")
}
