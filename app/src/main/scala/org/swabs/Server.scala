package org.swabs

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.kernel.Resource
import cats.implicits.catsSyntaxApply
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import fs2.Stream
import org.http4s.dsl._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.swabs.app.Routes

object Server extends IOApp.Simple with Http4sDsl[IO] {

  final case class ServerConfig(host: Host, port: Port)

  override def run: IO[Unit] = IO(startServer).flatMap(_.compile.drain)

  private def startServer: Stream[IO, Nothing] =
    (for {
      serverURI    <- Stream.eval(Config.serverURI)
      httpApp       = Router("/api" -> Routes.routes).orNotFound
      finalHttpApp  = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)
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

/*
*
  /*
  import org.swabs.app.models.User
  import org.swabs.app.models.UserHistory
  import org.swabs.app.models.UserHistory._
  import org.swabs.core.redis.{Client => RedisClient}
  import play.api.libs.json.Json
  override def run: IO[Unit] = {
    // test
    for {
      uri      <- Config.redisUri
      client    = RedisClient(uri)
      user      = User("derp", UserHistory(Map("a" -> "b")))
      _        <- client.set(user.id, Json.stringify(Json.toJson(user.history)))
      user0    <- client.lookup(user.id)
      _        <- user0 match {
        case Some(user) => IO.println(user)
        case None => IO.unit
      }
    } yield ()
  }
   */
* */