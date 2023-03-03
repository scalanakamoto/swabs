package org.swabs.hub.session.models

import play.api.libs.json.Writes

private[hub] final case class SignUpVerifyException(
    message: String = "could not verify signature with pubkey"
) extends Exception {
  override def getMessage: String = message
}

private[hub] object SignUpVerifyException {
  implicit val writes: Writes[SignUpVerifyException] = Writes.of[String].contramap(_.message)
}
