package org.swabs.app.geo.models

import dev.profunktor.redis4cats.effects.Latitude
import dev.profunktor.redis4cats.effects.Longitude
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.profunktor.redis4cats.effects.{GeoLocation => RedisGeoLocation}
import org.swabs.core.models.user.UserId
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.Json

import java.util.UUID

class UserGeoLocationRequestSpec extends AnyWordSpec with Matchers {
  "UserGeoLocationRequest#toRedisGeoLocation" must {
    "work" in {
      val req = UserGeoLocationRequest(
        Coordinate(53.54682272325644, 10.004701498752866),
        UserId(UUID.fromString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"))
      )

      req.asRedisGeoLocation mustBe RedisGeoLocation(
        Longitude(53.54682272325644),
        Latitude(10.004701498752866),
        "d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"
      )
    }
  }

  "UserGeoLocationRequest#reads" must {
    "work" in {
      val req = UserGeoLocationRequest(
        Coordinate(53.54682272325644, 10.004701498752866),
        UserId(UUID.fromString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"))
      )

      Json.fromJson[UserGeoLocationRequest](Json.obj(fields =
        "coordinate" -> Json.obj(fields =
          "lon" -> JsNumber(53.54682272325644),
          "lat" -> JsNumber(10.004701498752866)
        ),
        "userId" -> JsString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604")
      )).asOpt mustBe Some(req)
    }
  }
}
