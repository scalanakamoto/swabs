package org.swabs.app.geo.models

import org.swabs.app.geo.models.GeoUnit.GeoUnit
import play.api.libs.json.Json
import play.api.libs.json.Reads

final case class LookupRadiusRequest(geoLocation: UserGeoLocation, distance: Double, unit: GeoUnit)

object LookupRadiusRequest {
  implicit val reads: Reads[LookupRadiusRequest] = Json.reads[LookupRadiusRequest]
}
