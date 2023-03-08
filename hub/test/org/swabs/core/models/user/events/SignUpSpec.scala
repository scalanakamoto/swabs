package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class SignUpSpec extends AnyWordSpec with Matchers {
  "Signup#reads" must {
    "work" in {
      Json.parse(""""2023-03-07T15:35:33"""").asOpt[SignUp] mustBe
        Some(SignUp("2023-03-07T15:35:33"))
    }
  }

  "Signup#writes" must {
    "work" in {
      Json.stringify(Json.toJson(SignUp("2023-03-07T15:35:33"))) mustBe
        """"2023-03-07T15:35:33""""
    }
  }
}
