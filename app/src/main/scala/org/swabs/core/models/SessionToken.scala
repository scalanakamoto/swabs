package org.swabs.core.models

import play.api.libs.json.Writes

final case class SessionToken(value: String) extends AnyVal

object SessionToken {
  implicit val writes: Writes[SessionToken] = Writes.of[String].contramap(_.value)
}
