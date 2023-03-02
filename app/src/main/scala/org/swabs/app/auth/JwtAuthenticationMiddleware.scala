package org.swabs.app.auth

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import org.http4s._
import org.http4s.headers.Cookie
import org.http4s.server.AuthMiddleware
import org.swabs.Config
import org.swabs.app.ServiceEngine.RedisEngine
import org.swabs.app.auth.models.JwtDecodingException
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm

import scala.util.Success

private[app] object JwtAuthenticationMiddleware extends RedisEngine {
  lazy val middleware: AuthMiddleware[IO, Unit] = AuthMiddleware(authUser)

  private final case class CookieNotFoundException(message: String) extends Exception {
    override def getMessage: String = message
  }

  private val authUser: Kleisli[OptionT[IO, *], Request[IO], Unit] =
    Kleisli(request => OptionT.liftF {
      val cookieContent = for {
        header <- request.headers.get[Cookie]
        cookie <- header.values.toList.find(_.name == "jwt")
      } yield cookie.content

      IO
        .fromOption(cookieContent)(CookieNotFoundException("cookie (jwt) was not found from in header"))
        .flatMap(decodeJwt)
    })

  private def decodeJwt(cookie: String): IO[Unit] =
    for {
      secret <- Config.secret
      _      <- Jwt.decodeRawAll(cookie, secret, JwtAlgorithm.HS256 :: Nil) match {
        case Success((_, claim, _)) if Jwt.validate(claim, secret, Seq(JwtAlgorithm.HS256)) =>
          IO.unit
        case _ =>
          IO.raiseError(JwtDecodingException)
      }
    } yield ()
}
