package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.{Latitude => GeoLatitude}
import play.api.libs.json.Reads
import play.api.libs.json.Writes

final case class UserLatitude(value: Double) extends AnyVal

object UserLatitude {
  implicit val reads: Reads[UserLatitude] = Reads.of[Double].map(UserLatitude.apply)
  implicit val writes: Writes[UserLatitude] = Writes.of[Double].contramap(_.value)

  implicit class RichUserLatitude(userLatitude: UserLatitude) {
    val toGeoLatitude: GeoLatitude = GeoLatitude(userLatitude.value)
  }
}
