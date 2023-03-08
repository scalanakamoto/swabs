package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.GeoRadiusResult
import org.swabs.core.models.user.UserId
import play.api.libs.json.Json
import play.api.libs.json.Writes

private[app] final case class UserGeoRadiusResponse(
    userId: UserId,
    dist: Double,
    geoHash: Long,
    coordinate: Coordinate
)

private[app] object UserGeoRadiusResponse {
  implicit val writes: Writes[UserGeoRadiusResponse] = Json.writes[UserGeoRadiusResponse]

  def from(geoRadiusResult: GeoRadiusResult[String]): Option[UserGeoRadiusResponse] =
    UserId.from(geoRadiusResult.value).map { userId =>
      UserGeoRadiusResponse(
        userId     = userId,
        dist       = geoRadiusResult.dist.value,
        geoHash    = geoRadiusResult.hash.value,
        coordinate = Coordinate.from(geoRadiusResult.coordinate)
      )
    }
}