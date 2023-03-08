package org.swabs.app.account.services

import cats.effect.IO
import cats.implicits.showInterpolator
import cats.implicits.toShow
import org.swabs.app.ServiceEngine
import org.swabs.core.models.errors.JsonParsingException
import org.swabs.core.models.user.User
import org.swabs.core.models.user.UserId
import org.swabs.core.models.user.errors.UserNotFoundException
import play.api.libs.json.Json

private[app] object UserService extends ServiceEngine.RedisEngine {
  // @todo pagination or streaming solution
  def getUser(userId: UserId): IO[User] =
    for {
      userRawStr <- redisClient
                      .flatMap(_.lookup(userHashCode, userId.show))
                      .handleErrorWith(_ => IO.raiseError(UserNotFoundException(userId)))
      user       <- IO.fromOption(Json.parse(userRawStr).asOpt[User])(JsonParsingException(show"json of $userId"))
    } yield  user

  def setUserEvents(user: User): IO[Unit] =
    for {
      found       <- getUser(user.userId)
      updatedUser  = found.update(user.events)
      _           <- redisClient.map(_.update(userHashCode, updatedUser.userId.show, updatedUser.asJsonString))
    } yield ()
}
