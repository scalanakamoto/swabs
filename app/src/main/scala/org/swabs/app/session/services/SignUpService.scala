package org.swabs.app.session.services

import cats.effect.IO
import cats.kernel.Monoid
import org.swabs.Config
import org.swabs.app.ServiceEngine
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.models.SignUpVerifyException
import org.swabs.core.models.UserToken
import org.swabs.core.models.UserModels.History
import org.swabs.core.redis.models.RedisKeys
import org.swabs.core.util.Verification
import play.api.libs.json.Json

import java.security.MessageDigest
import java.time.LocalDateTime

private[app] object SignUpService extends ServiceEngine.RedisEngine {
  def apply(signUp: SignUp): IO[UserToken] =
    for {
      isVerified <- IO(Verification.sigWithPubkey(signUp.signature, signUp.publicKey))
      _          <- IO.unlessA(isVerified)(IO.raiseError(SignUpVerifyException()))

      token      <- Config.tokenSalt.map(createToken(signUp, _))
      history    <- createSignUpHistory

      _          <- redisClient.map(_.signup(token, history))
    } yield UserToken(token)

  private val createSignUpHistory: IO[String] =
    for {
      now     <- IO(LocalDateTime.now(clock))
      history  = History(Map(RedisKeys.SIGNUP -> now.toString, RedisKeys.HISTORY -> Monoid.empty[String]))
      jsonStr  = Json.stringify(Json.toJson(history))
    } yield jsonStr

  private def createToken(signUp: SignUp, salt: String): String =
    MessageDigest
      .getInstance("SHA-256")
      .digest((signUp.signature + signUp.publicKey + salt).getBytes("UTF-8"))
      .map("%02x".format(_))
      .mkString
}
