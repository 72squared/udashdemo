package udashdemo

import org.joda.time.DateTime

import scala.concurrent.Future

case class User(name:String, createdAt: DateTime, createdBy: String)

object User extends EnhancedRestDataCompanion[User]


case class ServiceContext(requestedBy: String)
object ServiceContext extends EnhancedRestDataCompanion[ServiceContext]

trait UserApi {
  /** Returns newly created user */
  def createUser(name: String)(implicit sc: ServiceContext): Future[User]
  def boom: Future[Int]
}



object UserApi extends EnhancedRestApiCompanion[UserApi]




