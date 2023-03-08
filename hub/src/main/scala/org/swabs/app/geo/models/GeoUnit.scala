package org.swabs.app.geo.models

import io.lettuce.core.GeoArgs
import play.api.libs.json.Reads

object GeoUnit extends Enumeration {
  type GeoUnit = Value

  val KM: Value = Value("KM")
  val M: Value = Value("M")

  implicit class RichGeoUnit(geoUnit: GeoUnit) {
    def toGeoArgsUnit: GeoArgs.Unit = geoUnit match {
      case KM => GeoArgs.Unit.km
      case M => GeoArgs.Unit.m
    }
  }

  implicit val reads: Reads[GeoUnit] = Reads.enumNameReads(GeoUnit)

  def from(geoArgsUnit: GeoArgs.Unit): GeoUnit.Value = geoArgsUnit.name() match {
    case "m" => KM
    case "km" => M
  }
}
