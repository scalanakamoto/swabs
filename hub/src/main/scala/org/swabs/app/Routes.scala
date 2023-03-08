package org.swabs.app

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import org.http4s._
import org.http4s.dsl._
import org.http4s.play.PlayEntityCodec.playEntityDecoder
import org.http4s.play.PlayEntityCodec.playEntityEncoder
import org.swabs.app.account.services.UserService
import org.swabs.app.auth.JwtAuthenticationMiddleware
import org.swabs.app.geo.GeoService
import org.swabs.app.geo.models.LookupRadiusRequest
import org.swabs.app.geo.models.UserGeoLocation
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.services.SignInService
import org.swabs.app.session.services.SignUpService
import org.swabs.core.models.user.User
import org.swabs.core.models.user.UserId

object Routes extends Http4sDsl[IO] {
  // todo signature openapi swagger: Base64 encoded signature and pubkey
  private def signUp: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "session" / "sign-up" =>
      for {
        signUp <- req.as[SignUp]
        userId <- SignUpService.create(signUp)
      } yield Response[IO](Ok).withEntity(userId)
  }

  private def signIn: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "session" / "sign-in" =>
      for {
        userId <- req.as[UserId]
        jwt    <- SignInService.getNewJWT(userId)
        resp   <- Ok().map(_.addCookie("jwt", jwt.value))
      } yield resp
  }

  def sessionRoutes: Kleisli[OptionT[IO, *], Request[IO], Response[IO]] = signUp <+> signIn

  private def getUser: AuthedRoutes[Unit, IO] = AuthedRoutes.of {
    case authReq@POST -> Root / "user" as _ =>
      for {
        userId <- authReq.req.as[UserId]
        user   <- UserService.getUser(userId)
      } yield Response[IO](Ok).withEntity(user)
  }

  private def setUserEvents: AuthedRoutes[Unit, IO] = AuthedRoutes.of {
    case authReq@POST -> Root / "user" / "update-events" as _ =>
      for {
        user <- authReq.req.as[User]
        _    <- UserService.setUserEvents(user)
      } yield Response[IO](Created)
  }

  def userRoutes: Kleisli[OptionT[IO, *], Request[IO], Response[IO]] =
      JwtAuthenticationMiddleware.middleware(getUser) <+>
        JwtAuthenticationMiddleware.middleware(setUserEvents)

  private def setUserPosition: AuthedRoutes[Unit, IO] = AuthedRoutes.of {
    case authReq@POST -> Root / "user" / "geo" as _ =>
      for {
        geo <- authReq.req.as[UserGeoLocation]
        _   <- GeoService.setPosition(geo)
      } yield Response[IO](Created)
  }

  private def lookupRadius: AuthedRoutes[Unit, IO] = AuthedRoutes.of {
    case authReq@POST -> Root / "user" / "geo" / "radius" as _ =>
      for {
        req  <- authReq.req.as[LookupRadiusRequest]
        json <- GeoService.lookupRadius(req)
      } yield Response[IO](Ok).withEntity(json)
  }

  def geoRoutes: Kleisli[OptionT[IO, *], Request[IO], Response[IO]] =
    JwtAuthenticationMiddleware.middleware(setUserPosition) <+>
      JwtAuthenticationMiddleware.middleware(lookupRadius)
}
