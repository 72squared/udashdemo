package udashdemo

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
import io.udash.rest._

import scala.concurrent.Future
import org.joda.time.DateTime


abstract class EnhancedRestDataCompanion[T](implicit
                                            instances: MacroInstances[EnhancedRestImplicits,() => GenCodec[T]]
                                           ) extends {
  implicit lazy val codec: GenCodec[T] = instances(EnhancedRestImplicits, this).apply()
}

trait EnhancedRestImplicits extends DefaultRestImplicits {
  implicit val dateTimeCodec: GenCodec[DateTime] = GenCodec.nullableSimple(i => DateTime.parse(i.readString()), (o, v) => o.writeString(v.toString))
  implicit val codedExceptionCodec: GenCodec[CodedException] = GenCodec.nullableSimple(i => throw new CodedException(i.readString()), (o, v) => o.writeString(v.code))
}

object EnhancedRestImplicits extends EnhancedRestImplicits

case class User(name:String, createdAt: DateTime)

object User extends EnhancedRestDataCompanion[User]

class CodedException(val code: String) extends Exception


trait UserApi {
  /** Returns newly created user */
  def createUser(name: String): Future[User]
  def getNow: Future[DateTime]
  def boom: Future[Int]
}

object UserApi extends RestApiCompanion[EnhancedRestImplicits, UserApi](EnhancedRestImplicits)