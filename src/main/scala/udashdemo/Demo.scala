package udashdemo

import io.udash.rest.SttpRestClient
import sttp.client.SttpBackend

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Demo extends App{
    // allocate an STTP backend
    implicit val sttpBackend: SttpBackend[Future, Nothing, Nothing] = SttpRestClient.defaultBackend()

    // obtain a "proxy" instance of UserApi
    val client: UserApi = SttpRestClient[UserApi]("http://127.0.0.1:9090/api")

    // make a remote REST call
    val result: Future[User] = client.createUser("Fred")

    // use whatever execution context is appropriate
    import scala.concurrent.ExecutionContext.Implicits.global

    // do something with the result
    result.onComplete {
        case Success(user) => println(s"User ${user.name} created with dt of: ${user.createdAt}")
        case Failure(cause) => cause.printStackTrace()
    }


    // just wait until the Future is complete so that main thread doesn't finish prematurely
    Await.ready(result, 10.seconds)
}