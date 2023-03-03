package org.swabs.app.session.services

import cats.effect.IO
import cats.implicits.toShow
import org.swabs.app.ServiceEngine
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.models.SignUpVerifyException
import org.swabs.core.models.user.User
import org.swabs.core.models.user.UserId
import org.swabs.core.models.user.events.Events
import org.swabs.core.models.user.events.{SignUp => CoreSignUp}
import org.swabs.util.SignatureWithPubkey

import java.util.UUID

private[app] object SignUpService extends ServiceEngine.RedisEngine {
  def create(signUp: SignUp): IO[UserId] =
    for {
      isVerified <- IO(SignatureWithPubkey.verify(signUp.signature, signUp.publicKey))
      _          <- IO.unlessA(isVerified)(IO.raiseError(SignUpVerifyException()))

      uuid       <- IO(UUID.randomUUID())
      userId      = UserId(uuid)
      user        = User(userId = userId, events = Events(CoreSignUp.fromClock, Nil))
      userStr    <- IO(user.asJsonString)

      _          <- redisClient.map(_.signup(userHashCode, uuid.show, userStr))
    } yield userId
}
