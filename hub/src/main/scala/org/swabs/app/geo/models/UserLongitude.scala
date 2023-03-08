package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.{Longitude => GeoLongitude}
import play.api.libs.json.Reads
import play.api.libs.json.Writes

final case class UserLongitude(value: Double) extends AnyVal

object UserLongitude {
  implicit val reads: Reads[UserLongitude] = Reads.of[Double].map(UserLongitude.apply)
  implicit val writes: Writes[UserLongitude] = Writes.of[Double].contramap(_.value)

  implicit class RichUserLongitude(userLongitude: UserLongitude) {
    val toGeoLongitude: GeoLongitude = GeoLongitude(userLongitude.value)
  }
}
