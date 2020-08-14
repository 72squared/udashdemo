package udashdemo

import org.joda.time.DateTime

import scala.concurrent.Future

class UserApiImpl extends UserApi {
  def createUser(name: String)(implicit sc: ServiceContext): Future[User] =
    Future.successful(User(name, createdAt=DateTime.now, createdBy = sc.requestedBy))

  override def boom: Future[Int] = throw new CodedException("boooooom!!!")
}
