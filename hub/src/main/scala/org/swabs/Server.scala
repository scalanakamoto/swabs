package org.swabs

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.kernel.Resource
import cats.implicits.catsSyntaxApply
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import fs2.Stream
import org.http4s.dsl._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.swabs.app.Routes._

object Server extends IOApp.Simple with Http4sDsl[IO] {
  final case class ServerConfig(host: Host, port: Port)

  override def run: IO[Unit] = IO(startServer).flatMap(_.compile.drain)

  private def startServer: Stream[IO, Nothing] =
    (for {
      serverURI    <- Stream.eval(Config.serverConfig)
      routes        = sessionRoutes <+> userRoutes <+> geoRoutes
      finalHttpApp  = Logger.httpApp(logHeaders = true, logBody = true)(Router("/api" -> routes).orNotFound)
      exitCode     <- Stream.resource(
                        EmberServerBuilder
                          .default[IO]
                          .withHost(serverURI.host)
                          .withPort(serverURI.port)
                          .withHttpApp(finalHttpApp)
                          .build *>
                            Resource.eval(IO.never)
                      )
    } yield exitCode).drain
}
