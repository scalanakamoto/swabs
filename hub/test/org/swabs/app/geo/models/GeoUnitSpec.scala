package org.swabs.app.geo.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.app.geo.models.GeoUnit.GeoUnit
import play.api.libs.json.JsString
import play.api.libs.json.Json
import io.lettuce.core.GeoArgs

class GeoUnitSpec extends AnyWordSpec with Matchers {
  "GeoUnit#defined properly" must {
    "work" in {
      GeoUnit.withName("KM") mustBe GeoUnit.KM
      GeoUnit.withName("M") mustBe GeoUnit.M
    }
  }

  "GeoUnit#reads" must {
    "work" in {
      JsString("KM").validate[GeoUnit].asOpt mustBe Some(GeoUnit.KM)
      JsString("M").validate[GeoUnit].asOpt mustBe Some(GeoUnit.M)
    }
  }

  "GeoUnit#writes" must {
    "work" in {
      Json.toJson(GeoUnit.KM) mustBe JsString("KM")
      Json.toJson(GeoUnit.M) mustBe JsString("M")
    }
  }

  "GeoUnit#from" must {
    "work" in {
      GeoUnit.from(GeoArgs.Unit.m) mustBe GeoUnit.M
      GeoUnit.from(GeoArgs.Unit.km) mustBe GeoUnit.KM
    }
  }
}
