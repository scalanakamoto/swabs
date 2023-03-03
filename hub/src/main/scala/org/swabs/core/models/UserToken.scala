package org.swabs.core.models

import cats.Show
import play.api.libs.json.Reads
import play.api.libs.json.Writes

final case class UserToken(value: String) extends AnyVal

object UserToken {
  implicit val show: Show[UserToken] = _.value
  implicit val writes: Writes[UserToken] = Writes.of[String].contramap(_.value)
  implicit val reads: Reads[UserToken] = Reads.of[String].map(UserToken.apply)
}
