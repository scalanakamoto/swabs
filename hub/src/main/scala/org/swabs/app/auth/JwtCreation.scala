package org.swabs.app.auth

import cats.effect.IO
import org.swabs.Config
import org.swabs.app.session.services.SignInService.clock
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtClaim

import java.time.LocalDateTime
import java.time.ZoneOffset

private[app] trait JwtCreation {
  private final val hour = 3600

  private implicit class RichLocalDateTime(ldt: => LocalDateTime) {
    def toUTCSec: Long = ldt.toEpochSecond(ZoneOffset.UTC)
  }

  def createJWT(subject: String): IO[String] =
    for {
      issuedAt   <- IO(LocalDateTime.now(clock))
      expiration  = issuedAt.plusSeconds(hour)
      claim = JwtClaim(
        subject    = Some(subject),
        issuedAt   = Some(issuedAt.toUTCSec),
        expiration = Some(expiration.toUTCSec)
      )
      jwt        <- Config.secret.map(Jwt.encode(claim, _, JwtAlgorithm.HS256))
    } yield jwt
}
