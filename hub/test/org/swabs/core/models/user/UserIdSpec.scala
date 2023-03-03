package org.swabs.core.models.user

import cats.implicits.toShow
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.JsString
import play.api.libs.json.Json

import java.util.UUID

class UserIdSpec extends AnyWordSpec with Matchers {
  "UserId#reads" must {
    "work" in {
      Json.parse(""""d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"""").asOpt[UserId] mustBe
        Some(UserId(UUID.fromString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604")))
    }
  }

  "UserId#writes" must {
    "work" in {
      Json.toJson(UserId(UUID.fromString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"))) mustBe
        JsString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604")
    }
  }

  "UserId#parse" must {
    "work and fail" in {
      UserId.parse.parse("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604").toOption.nonEmpty mustBe true

      UserId.parse.parse("derp").toOption.nonEmpty mustBe false
    }
  }

  "User#show" must {
    "work" in {
      UserId(UUID.fromString("d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604")).show mustBe
        "d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"
    }
  }
}
