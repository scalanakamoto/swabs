package org.swabs.core.models.user.events

import cats.implicits._
import org.swabs.util.GlobalDateTimeFormat
import org.swabs.util.Parse
import play.api.libs.json.Reads
import play.api.libs.json.Writes

import java.time.Clock
import java.time.LocalDateTime

final case class SignUp(value: String)

object SignUp {
  def fromClock(implicit clock: Clock): SignUp =
    new SignUp(LocalDateTime.now(clock).format(GlobalDateTimeFormat.apply))

  implicit val writes: Writes[SignUp] =
    Writes.of[String].contramap(s => LocalDateTime.parse(s.value, GlobalDateTimeFormat.apply).toString)
  implicit val parse: Parse[SignUp] =
    Parse.localDateTimeParser(GlobalDateTimeFormat.apply).map(ldt => SignUp(ldt.toString))
  implicit val reads: Reads[SignUp] = Parse.reads
}
