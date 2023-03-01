package org.swabs.app

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import play.api.libs.json.{Json => PlayJson}
import org.http4s._
import org.http4s.dsl._
import org.http4s.play.PlayEntityCodec.playEntityDecoder
import org.swabs.app.auth.AuthenticationMiddleware
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.services.SignUpService
import org.swabs.core.models.SessionToken

object Routes extends Http4sDsl[IO]{

  // @todo landing page with signup
  private def getLanding: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "" =>
      Ok("hello")
  }

  private def signUp: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "signUp" =>
      for {
        signUp <- req.as[SignUp]
        token  <- SignUpService(signUp)
        resp    = Response[IO](Ok).withEntity(PlayJson.stringify(PlayJson.toJson(token)))
      } yield resp
  }

  private def signIn: AuthedRoutes[SessionToken, IO] = AuthedRoutes.of {
    case GET -> Root / "signIn" as _ => Ok()
  }

  def routes: Kleisli[OptionT[IO, *], Request[IO], Response[IO]] =
    getLanding <+> signUp <+> AuthenticationMiddleware.middleware(signIn)
}
