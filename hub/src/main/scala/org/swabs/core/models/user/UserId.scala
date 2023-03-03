package org.swabs.core.models.user

import cats.Show
import cats.implicits._
import org.swabs.util.Parse
import play.api.libs.json.Reads
import play.api.libs.json.Writes

import java.util.UUID

final case class UserId(value: UUID) extends AnyVal

object UserId {
  implicit val show: Show[UserId] = _.value.toString
  implicit val writes: Writes[UserId] = Writes.of[UUID].contramap(_.value)
  implicit val parse: Parse[UserId] = Parse.uuidParse.map(UserId.apply)
  implicit val reads: Reads[UserId] = Parse.reads

  def from(s: String): Option[UserId] = parse.parse(s).toOption
}
