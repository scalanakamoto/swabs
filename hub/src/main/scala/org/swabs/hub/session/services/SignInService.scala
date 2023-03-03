package org.swabs.hub.session.services

import cats.effect.IO
import org.swabs.Config
import org.swabs.hub.ServiceEngine
import org.swabs.hub.auth.models.JwtToken
import org.swabs.core.models.UserNotFoundException
import org.swabs.core.models.UserToken
import pdi.jwt.Jwt
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtClaim

import java.time.LocalDateTime
import java.time.ZoneOffset

private[hub] object SignInService extends ServiceEngine.RedisEngine {
  def apply(userToken: UserToken): IO[JwtToken] =
    for {
      isUser <- redisClient.flatMap(_.lookup(userToken.value))
      _      <- IO.whenA(isUser.isEmpty)(IO.raiseError(UserNotFoundException(userToken)))

      jwt    <- createJWT(userToken).map(JwtToken.apply)
    } yield jwt

  private def createJWT(userToken: UserToken): IO[String] =
    for {
      issuedAt   <- IO(LocalDateTime.now(clock))
      expiration  = issuedAt.plusSeconds(3600)
      claim = JwtClaim(
        subject    = Some(userToken.value),
        issuedAt   = Some(issuedAt.toUTCSec),
        expiration = Some(expiration.toUTCSec)
      )
      jwt        <- Config.secret.map(Jwt.encode(claim, _, JwtAlgorithm.HS256))
    } yield jwt

  private implicit class RichLocalDateTime(ldt: => LocalDateTime) {
    def toUTCSec: Long = ldt.toEpochSecond(ZoneOffset.UTC)
  }
}
