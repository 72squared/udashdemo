package udashdemo

import io.udash.rest.RestServlet
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

object Server extends App {
    // translate UserApiImpl into a Servlet
    val userApiServlet = RestServlet[UserApi](new UserApiImpl)

    // do all the Jetty related plumbing
    val server = new org.eclipse.jetty.server.Server(9090)
    val handler = new ServletContextHandler
    handler.addServlet(new ServletHolder(userApiServlet), "/api/*")
    server.setHandler(handler)
    server.start()
    server.join()

}