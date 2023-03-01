package org.swabs.app.session.services

import cats.effect.IO
import org.swabs.app.Engine
import org.swabs.app.session.models.SignUp
import org.swabs.app.session.models.SignUpVerifyException
import org.swabs.app.session.util.Verification.sigWithPubkey
import org.swabs.core.models.RedisKeys
import org.swabs.core.models.SessionToken
import org.swabs.core.models.UserModels.History
import play.api.libs.json.Json

import java.security.MessageDigest
import java.time.LocalDateTime

object SignUpService extends Engine.RedisEngine {
  def apply(signUp: SignUp): IO[SessionToken] =
    for {
      isVerified <- IO(sigWithPubkey(signUp.signature, signUp.publicKey))
      _          <- IO.unlessA(isVerified)(IO.raiseError(SignUpVerifyException()))
      token      <- IO(createToken(signUp))
      history    <- createSignUpHistory
      _          <- redisClient.map(_.signup(token, history))
    } yield SessionToken(token)

  private val createSignUpHistory: IO[String] = {
    for {
      now     <- IO(LocalDateTime.now(clock))
      history  = History(Map(
        RedisKeys.SIGNUP -> now.toString,
        RedisKeys.HISTORY -> ""
      ))
      jsonStr  = Json.stringify(Json.toJson(history))
    } yield jsonStr
  }

  private def createToken(signUp: SignUp): String =
    MessageDigest.getInstance("SHA-256")
      .digest((signUp.signature + signUp.publicKey).getBytes("UTF-8"))
      .map("%02x".format(_))
      .mkString
}
