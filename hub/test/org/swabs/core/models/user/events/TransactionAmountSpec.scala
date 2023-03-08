package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.events.Transactions.TransactionAmount
import play.api.libs.json.JsNumber
import play.api.libs.json.Json


class TransactionAmountSpec extends AnyWordSpec with Matchers {
  "TransactionAmount#reads" must {
    "work" in {
      JsNumber(123.123456791234567).asOpt[TransactionAmount] mustBe Some(TransactionAmount(123.123456791234567))
      JsNumber(2100000000000000L).asOpt[TransactionAmount] mustBe Some(TransactionAmount(2100000000000000L))
      JsNumber(123.123456791234567).asOpt[TransactionAmount] mustBe Some(TransactionAmount(123.123456791234568))
    }
  }

  "TransactionAmount#writes" must {
    "work" in {
      Json.toJson(TransactionAmount(123.123456791234567)) mustBe JsNumber(123.123456791234567)
      Json.toJson(TransactionAmount(2100000000000000L)) mustBe JsNumber(2100000000000000L)
      Json.toJson(TransactionAmount(123.123456791234568)) mustBe JsNumber(123.123456791234567)
    }
  }
}
