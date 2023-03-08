package org.swabs.app.geo.models

import cats.implicits.toShow
import dev.profunktor.redis4cats.effects.{GeoLocation => RedisGeoLocation}
import org.swabs.core.models.user.UserId
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

final case class UserGeoLocation(lon: UserLongitude, lat: UserLatitude, userId: UserId) {
  def toRedisGeoLocation: RedisGeoLocation[String] =
    RedisGeoLocation(lon.toGeoLongitude, lat.toGeoLatitude, userId.show)
}

object UserGeoLocation {
  // @todo parse & validate
  implicit val reads: Reads[UserGeoLocation] = Json.reads[UserGeoLocation]
  implicit val writes: Writes[UserGeoLocation] = Json.writes[UserGeoLocation]
}
