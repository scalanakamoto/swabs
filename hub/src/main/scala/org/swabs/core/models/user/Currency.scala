package org.swabs.core.models.user

import org.swabs.util.Parse
import play.api.libs.json.Reads
import play.api.libs.json.Writes

object Currency extends Enumeration {
  type Currency = Value

  val BTC = Value("BTC")
  val SATS = Value("SATS")
  val EUR = Value("EUR")
  val XMR = Value("XMR")
  val USD = Value("USD")
  val CHF = Value("CHF")

  implicit val writes: Writes[Currency] = Writes.enumNameWrites
  implicit val parse: Parse[Currency] = Parse.enumNameParse(Currency)
  implicit val reads: Reads[Currency] = Parse.reads
}
