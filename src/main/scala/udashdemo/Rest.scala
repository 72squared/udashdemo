package udashdemo

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
import io.udash.rest._
import io.udash.rest.raw.{RawRest, RestResponse}

import scala.concurrent.Future
import org.joda.time.DateTime

import scala.util.{Failure, Success, Try}
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.raw._

abstract class EnhancedRestDataCompanion[T](implicit
                                            instances: MacroInstances[EnhancedRestImplicits,() => GenCodec[T]]
                                           ) extends {
  implicit lazy val codec: GenCodec[T] = instances(EnhancedRestImplicits, this).apply()
}

trait EnhancedRestImplicits extends DefaultRestImplicits {
  implicit val dateTimeCodec: GenCodec[DateTime] = GenCodec.nullableSimple(i => DateTime.parse(i.readString()), (o, v) => o.writeString(v.toString))
  implicit val codedExceptionCodec: GenCodec[CodedException] = GenCodec.nullableSimple(i => throw new CodedException(i.readString()), (o, v) => o.writeString(v.code))


  implicit def futureAsAsyncResponse[T](
                                         implicit asResponse: AsRaw[RestResponse, T]
                                       ): AsRaw[RawRest.Async[RestResponse], Try[Future[T]]] =
  // Future is wrapped into Try because a REST API method can throw exceptions without wrapping them into failed Future
    (triedFuture: Try[Future[T]]) => {
      // we are reusing the original implicit
      val origResult: RawRest.Async[RestResponse] =
        RestResponse.effectToAsyncResp[Future, T].asRaw(triedFuture)
      // and adding additional exception translation
      RawRest.transformAsync(origResult) {
        case Failure(e: CodedException) =>
          val respHeaders = IMapping(
            Seq("X-Coded-Exception" -> PlainValue(e.code)),
          )
          Success(RestResponse(500, respHeaders, HttpBody.Empty))
        case v => v
      }
    }

  implicit def asyncResponseAsFuture[T](
                                         implicit fromResponse: AsReal[RestResponse, T]
                                       ): AsReal[RawRest.Async[RestResponse], Try[Future[T]]] =
    (asyncResponse: RawRest.Async[RestResponse]) => {
      // add additional exception translation
      val withExtractedException = RawRest.transformAsync(asyncResponse) {
        case res @ Success(RestResponse(500, headers, _)) =>
          // extracting the header
          headers.lift("X-Coded-Exception").map(_.value) match {
            case Some(code) => Failure(new CodedException(code))
            case None => res // not a CodedException failure
          }
        case res => res
      }
      // now use the original implicit
      RestResponse.effectFromAsyncResp[Future, T].asReal(withExtractedException)
    }
}

object EnhancedRestImplicits extends EnhancedRestImplicits

abstract class EnhancedRestApiCompanion[Real](implicit inst: MacroInstances[EnhancedRestImplicits, FullInstances[Real]]) extends RestApiCompanion[EnhancedRestImplicits, Real](EnhancedRestImplicits)






