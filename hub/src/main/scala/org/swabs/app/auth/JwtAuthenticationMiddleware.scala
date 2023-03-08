package org.swabs.app.auth

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import org.http4s._
import org.http4s.headers.Cookie
import org.http4s.server.AuthMiddleware
import org.swabs.Config
import org.swabs.app.auth.models.CookieNotFoundException
import org.swabs.app.auth.models.JwtDecodingException
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm

import scala.util.Success

private[app] object JwtAuthenticationMiddleware {
  lazy val middleware: AuthMiddleware[IO, Unit] = AuthMiddleware(authUser)

  private val authUser: Kleisli[OptionT[IO, *], Request[IO], Unit] =
    Kleisli(request => OptionT.liftF {
      val cookieContent = for {
        header  <- request.headers.get[Cookie]
        content <- header.values.toList.find(_.name == "jwt").map(_.content)
      } yield content

      IO
        .fromOption(cookieContent)(CookieNotFoundException("cookie (jwt) was not found from in header"))
        .flatMap(decodeJwt)
    })

  private def decodeJwt(cookie: String): IO[Unit] =
    Config.secret.flatMap { secret =>
      Jwt.decodeRawAll(cookie, secret, JwtAlgorithm.HS256 :: Nil) match {
        case Success((_, claim, _)) if Jwt.isValid(claim, secret, Seq(JwtAlgorithm.HS256)) =>
          IO.unit
        case _ =>
          IO.raiseError(JwtDecodingException)
      }
    }
}
