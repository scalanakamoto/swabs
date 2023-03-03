package org.swabs.hub.session.models

import play.api.libs.json.Json
import play.api.libs.json.Reads

private[hub] final case class SignUp(signature: String, publicKey: String)

private[hub] object SignUp {
  implicit val reads: Reads[SignUp] = Json.reads[SignUp]
}
