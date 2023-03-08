package org.swabs.app.session.services

import cats.effect.IO
import cats.implicits.toShow
import org.swabs.app.ServiceEngine
import org.swabs.app.auth.JwtCreation
import org.swabs.app.auth.models.JwtToken
import org.swabs.core.models.user.UserId
import org.swabs.core.models.user.errors.UserNotFoundException

private[app] object SignInService extends ServiceEngine.RedisEngine with JwtCreation {
  def getNewJWT(userToken: UserId): IO[JwtToken] =
    for {
      _   <- redisClient
               .flatMap(_.lookup(userHashCode, userToken.value.show))
               .handleErrorWith(_ => IO.raiseError(UserNotFoundException(userToken)))
      jwt <- createJWT(userToken.value.show).map(JwtToken.apply)
    } yield jwt
}
