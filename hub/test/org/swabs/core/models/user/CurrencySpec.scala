package org.swabs.core.models.user

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.Currency.Currency
import play.api.libs.json.JsString
import play.api.libs.json.Json

class CurrencySpec extends AnyWordSpec with Matchers {
  "Currency#defined properly" must {
    "work" in {
      Currency.withName("BTC") mustBe Currency.BTC
      Currency.withName("SATS") mustBe Currency.SATS
      Currency.withName("EUR") mustBe Currency.EUR
      Currency.withName("XMR") mustBe Currency.XMR
      Currency.withName("CHF") mustBe Currency.CHF
      Currency.withName("USD") mustBe Currency.USD
    }
  }

  "Currency#reads" must {
    "work" in {
      JsString("BTC").validate[Currency].asOpt mustBe Some(Currency.BTC)
      JsString("SATS").validate[Currency].asOpt mustBe Some(Currency.SATS)
      JsString("EUR").validate[Currency].asOpt mustBe Some(Currency.EUR)
      JsString("XMR").validate[Currency].asOpt mustBe Some(Currency.XMR)
      JsString("CHF").validate[Currency].asOpt mustBe Some(Currency.CHF)
      JsString("USD").validate[Currency].asOpt mustBe Some(Currency.USD)
    }
  }

  "Currency#writes" must {
    "work" in {
      Json.toJson(Currency.BTC) mustBe JsString("BTC")
      Json.toJson(Currency.SATS) mustBe JsString("SATS")
      Json.toJson(Currency.EUR) mustBe JsString("EUR")
      Json.toJson(Currency.XMR) mustBe JsString("XMR")
      Json.toJson(Currency.CHF) mustBe JsString("CHF")
      Json.toJson(Currency.USD) mustBe JsString("USD")
    }
  }
}
