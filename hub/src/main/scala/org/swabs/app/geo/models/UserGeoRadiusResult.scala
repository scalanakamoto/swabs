package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.GeoRadiusResult
import org.swabs.core.models.user.UserId
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class UserGeoRadiusResult(
    userId: UserId,
    dist: Double,
    geoHash: Long,
    coordinate: Coordinate
)

object UserGeoRadiusResult {
  implicit val writes: Writes[UserGeoRadiusResult] = Json.writes[UserGeoRadiusResult]

  def from(geoRadiusResult: GeoRadiusResult[String]): Option[UserGeoRadiusResult] =
    UserId.from(geoRadiusResult.value).map { userId =>
      UserGeoRadiusResult(
        userId = userId,
        dist = geoRadiusResult.dist.value,
        geoHash = geoRadiusResult.hash.value,
        coordinate = Coordinate.from(geoRadiusResult.coordinate)
      )
    }
}
