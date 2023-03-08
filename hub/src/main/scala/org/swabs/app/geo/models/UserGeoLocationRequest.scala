package org.swabs.app.geo.models

import cats.implicits.toShow
import dev.profunktor.redis4cats.effects.{GeoLocation => RedisGeoLocation}
import org.swabs.core.models.user.UserId
import play.api.libs.json.Json
import play.api.libs.json.Reads

private[app] final case class UserGeoLocationRequest(coordinate: Coordinate, userId: UserId) {
  val asRedisGeoLocation: RedisGeoLocation[String] =
    RedisGeoLocation(coordinate.asGeoLongitude, coordinate.asGeoLatitude, userId.show)
}

private[app] object UserGeoLocationRequest {
  implicit val reads: Reads[UserGeoLocationRequest] = Json.reads[UserGeoLocationRequest]
}
