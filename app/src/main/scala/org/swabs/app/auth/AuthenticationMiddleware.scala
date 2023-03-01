package org.swabs.app.auth

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import org.http4s._
import org.http4s.headers.Cookie
import org.http4s.server.AuthMiddleware
import org.swabs.app.Engine.RedisEngine
import org.swabs.core.models.SessionToken

object AuthenticationMiddleware extends RedisEngine {
  private val authUser: Kleisli[OptionT[IO, *], Request[IO], SessionToken] =
    Kleisli(request => OptionT.liftF {
      val token = for {
        header <- request.headers.get[Cookie]
        cookie <- header.values.toList.find(_.name == "token")
      } yield cookie.content

      IO
        .fromOption(token)(new Throwable())
        .flatMap(token => redisClient.flatMap(_.lookup(token)))
        .flatMap(IO.fromOption(_)(new Throwable))
        .map(SessionToken.apply)
    })

  val middleware: AuthMiddleware[IO, SessionToken] = AuthMiddleware(authUser)
}
