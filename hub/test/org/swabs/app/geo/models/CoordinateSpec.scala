package org.swabs.app.geo.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.Json

class CoordinateSpec extends AnyWordSpec with Matchers {
  "Coordinate#reads" must {
    "work" in {
      Json.parse("""{"lon":53.54682272325644,"lat":10.004701498752866}""").asOpt[Coordinate] mustBe
        Some(Coordinate(53.54682272325644, 10.004701498752866))
      Json.parse("""{"lon":53.546822723256449,"lat":10.0047014987528669}""").asOpt[Coordinate] mustBe
        Some(Coordinate(53.54682272325645, 10.004701498752867))
    }
  }

  "Coordinate#writes" must {
    "work" in {
      Json.toJson(Coordinate(53.54682272325644, 10.004701498752866)) mustBe JsObject(List(
        "lon" -> JsNumber(53.54682272325644),
        "lat" -> JsNumber(10.004701498752866)
      ))
    }
  }
}
