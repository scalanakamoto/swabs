package org.swabs.app.session.models

import play.api.libs.json.Json
import play.api.libs.json.Reads

final case class SignUp(signature: String, publicKey: String)

object SignUp {
  implicit val reads: Reads[SignUp] = Json.reads[SignUp]
}
