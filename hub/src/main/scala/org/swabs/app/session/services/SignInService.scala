package org.swabs.app.session.services

import cats.effect.IO
import cats.implicits.toShow
import org.swabs.Config
import org.swabs.core.models.user.UserId
import org.swabs.core.models.user.errors.UserNotFoundException
import org.swabs.app.ServiceEngine
import org.swabs.app.auth.models.JwtToken
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtClaim

import java.time.LocalDateTime
import java.time.ZoneOffset

private[app] object SignInService extends ServiceEngine.RedisEngine {
  def getNewJWT(userToken: UserId): IO[JwtToken] =
    for {
      _ <- redisClient.flatMap(_.lookup(userHashCode, userToken.value.show)).handleErrorWith(
        _ => IO.raiseError(UserNotFoundException(userToken))
      )

      jwt <- create(userToken).map(JwtToken.apply)
    } yield jwt

  private implicit class RichLocalDateTime(ldt: => LocalDateTime) {
    def toUTCSec: Long = ldt.toEpochSecond(ZoneOffset.UTC)
  }

  private final val hour = 3600

  private def create(userToken: UserId): IO[String] =
    for {
      issuedAt   <- IO(LocalDateTime.now(clock))
      expiration  = issuedAt.plusSeconds(hour)
      claim = JwtClaim(
        subject    = Some(userToken.value.show),
        issuedAt   = Some(issuedAt.toUTCSec),
        expiration = Some(expiration.toUTCSec)
      )
      jwt        <- Config.secret.map(Jwt.encode(claim, _, JwtAlgorithm.HS256))
    } yield jwt
}
