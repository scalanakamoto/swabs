package org.swabs.core.models.money

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.money.Money._
import play.api.libs.json.Json

class MoneySpec extends AnyWordSpec with Matchers {
  "Money#reads" must {
    "work" in {
      Json.parse("""{"currency":"BTC","amount":1.0}""").as[BTC] mustBe BTC(1.0)
      Json.parse("""{"currency":"SATS","amount":123}""").as[SATS] mustBe SATS(123)
      Json.parse("""{"currency":"XMR","amount":1.0}""").as[XMR] mustBe XMR(1.0)
      Json.parse("""{"currency":"EUR","amount":123}""").as[EUR] mustBe EUR(123)
      Json.parse("""{"currency":"USD","amount":123}""").as[USD] mustBe USD(123)
      Json.parse("""{"currency":"CHF","amount":123}""").as[CHF] mustBe CHF(123)
    }
  }

  "Money#writes" must {
    "work" in {
      Json.stringify(Json.toJson(BTC(1.0))) mustBe """{"currency":"BTC","amount":1}"""
      Json.stringify(Json.toJson(SATS(123))) mustBe """{"currency":"SATS","amount":123}"""
      Json.stringify(Json.toJson(XMR(1.0))) mustBe """{"currency":"XMR","amount":1}"""
      Json.stringify(Json.toJson(EUR(123))) mustBe """{"currency":"EUR","amount":123}"""
      Json.stringify(Json.toJson(USD(123))) mustBe """{"currency":"USD","amount":123}"""
      Json.stringify(Json.toJson(CHF(123))) mustBe """{"currency":"CHF","amount":123}"""
    }
  }
}
