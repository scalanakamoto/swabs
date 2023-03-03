package org.swabs.hub.auth.models

import play.api.libs.json.Reads
import play.api.libs.json.Writes

private[hub] final case class JwtToken(value: String) extends AnyVal

private[hub] object JwtToken {
  implicit val writes: Writes[JwtToken] = Writes.of[String].contramap(_.value)
  implicit val reads: Reads[JwtToken] = Reads.of[String].map(JwtToken.apply)
}
