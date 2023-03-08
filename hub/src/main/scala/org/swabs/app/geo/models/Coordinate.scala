package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.GeoCoordinate
import dev.profunktor.redis4cats.effects.{Latitude => GeoLatitude}
import dev.profunktor.redis4cats.effects.{Longitude => GeoLongitude}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json._

private[app] final case class Coordinate(lon: Double, lat: Double)

private[app] object Coordinate {
  implicit class RichCoordinate(coordinate: => Coordinate) {
    val asGeoLongitude: GeoLongitude = GeoLongitude(coordinate.lon)
    val asGeoLatitude: GeoLatitude = GeoLatitude(coordinate.lat)
  }

  private val parse: Double => Double = BigDecimal(_).setScale(15, BigDecimal.RoundingMode.HALF_UP).doubleValue
  implicit val reads: Reads[Coordinate] =
    ((__ \ "lon").read[Double] and (__ \ "lat").read[Double])(Coordinate.apply _)
      .map(c => Coordinate(parse(c.lon), parse(c.lat)))
  implicit val writes: Writes[Coordinate] = Json.writes[Coordinate]

  def from(coordinate: GeoCoordinate): Coordinate = Coordinate(coordinate.x, coordinate.y)
}
