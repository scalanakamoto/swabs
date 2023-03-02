package org.swabs.app

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import play.api.libs.json.{Json => PlayJson}
import org.http4s._
import org.http4s.dsl._
import org.http4s.play.PlayEntityCodec.playEntityDecoder
import org.swabs.app.auth.JwtAuthenticationMiddleware
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.services.SignInService
import org.swabs.app.session.services.SignUpService
import org.swabs.core.models.UserToken

object Routes extends Http4sDsl[IO]{
  // todo signature openapi swagger: Base64 encoded signature and pubkey
  private def signUp: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "signUp" =>
      for {
        signUp <- req.as[SignUp]
        token  <- SignUpService(signUp)
      } yield Response[IO](Ok).withEntity(PlayJson.stringify(PlayJson.toJson(token)))
  }

  private def signIn: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "signIn" =>
      for {
        userToken <- req.as[UserToken]
        jwt       <- SignInService(userToken)
        resp      <- Ok().map(_.addCookie("jwt", jwt.value))
      } yield resp
  }

  // todo landing page with signup
  private def getLanding: AuthedRoutes[Unit, IO] = AuthedRoutes.of {
    case GET -> Root / "" as _ =>
      Ok("hello")
  }

  def routes: Kleisli[OptionT[IO, *], Request[IO], Response[IO]] =
    signUp <+>
      signIn <+>
      JwtAuthenticationMiddleware.middleware(getLanding)
}
