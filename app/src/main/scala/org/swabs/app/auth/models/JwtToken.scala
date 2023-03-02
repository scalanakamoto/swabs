package org.swabs.app.auth.models

import play.api.libs.json.Reads
import play.api.libs.json.Writes

private[app] final case class JwtToken(value: String) extends AnyVal

private[app] object JwtToken {
  implicit val writes: Writes[JwtToken] = Writes.of[String].contramap(_.value)
  implicit val reads: Reads[JwtToken] = Reads.of[String].map(JwtToken.apply)
}
