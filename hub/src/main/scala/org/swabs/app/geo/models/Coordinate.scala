package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.GeoCoordinate
import play.api.libs.json.Json
import play.api.libs.json.Writes

final case class Coordinate(x: Double, y: Double)

object Coordinate {
  implicit val writes: Writes[Coordinate] = Json.writes[Coordinate]

  def from(coordinate: GeoCoordinate): Coordinate = Coordinate(coordinate.x, coordinate.y)
}
