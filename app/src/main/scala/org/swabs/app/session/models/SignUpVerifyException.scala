package org.swabs.app.session.models

import play.api.libs.json.Writes

private[app] final case class SignUpVerifyException(
    message: String = "could not verify signature with pubkey"
) extends Exception {
  override def getMessage: String = message
}

private[app] object SignUpVerifyException {
  implicit val writes: Writes[SignUpVerifyException] = Writes.of[String].contramap(_.message)
}
