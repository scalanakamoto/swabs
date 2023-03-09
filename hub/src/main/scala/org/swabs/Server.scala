package org.swabs

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import org.http4s.dsl._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.swabs.app.Routes

object Server extends IOApp with Http4sDsl[IO] {
  final case class ServerConfig(host: Host, port: Port)

  override def run(args: List[String]): IO[ExitCode] = startServer

  private lazy val startServer: IO[ExitCode] =
    for {
      serverURI       <- Config.serverConfig
      grpcServer       = ServerBuilder.forPort(8080).build()
      channel          = ManagedChannelBuilder
                           .forAddress("localhost", 8080)
                           .usePlaintext()
                           .asInstanceOf[ManagedChannelBuilder[_]]
      routes           = Routes.routes(channel)
      finalHttpApp     = Logger.httpApp(logHeaders = true, logBody = true)(Router("/api" -> routes).orNotFound)
      httpServer       = EmberServerBuilder
                           .default[IO]
                           .withHost(serverURI.host)
                           .withPort(serverURI.port)
                           .withHttpApp(finalHttpApp)
                           .build

      grpcServerFiber <- IO(grpcServer.start()).start
      httpServerFiber <- httpServer.use(_ => IO.never).start
      _               <- grpcServerFiber.join
      _               <- httpServerFiber.join
    } yield ExitCode.Success
}
