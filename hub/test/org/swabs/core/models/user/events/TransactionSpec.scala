package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.Currency
import org.swabs.core.models.user.events.Transactions.Note
import org.swabs.core.models.user.events.Transactions.TransactionAmount
import org.swabs.core.models.user.events.Transactions.TransactionDateTime
import org.swabs.core.models.user.events.Transactions.Transaction
import play.api.libs.json.Json

import java.time.LocalDateTime

class TransactionSpec extends AnyWordSpec with Matchers {
  "Transaction#reads" must {
    "work" in {
      Json.parse(
        """{"dateTime":"2023-03-07T16:19:11","amount":123.0,"currency":"SATS","note":"satoshi was an agorist"}"""
      ).as[Transaction] mustBe (Transaction(
        dateTime = TransactionDateTime(LocalDateTime.parse("2023-03-07T16:19:11")),
        amount = TransactionAmount(123.0),
        currency = Currency.SATS,
        note = Note("satoshi was an agorist")
      ))
    }
  }

  "Transaction#writes" must {
    "work" in {
      Json.stringify(Json.toJson(Transaction(
        dateTime = TransactionDateTime(LocalDateTime.parse("2023-03-07T16:19:11")),
        amount = TransactionAmount(123.0),
        currency = Currency.SATS,
        note = Note("satoshi was an agorist")
      ))) mustBe
        """{"dateTime":"2023-03-07T16:19:11","amount":123,"currency":"SATS","note":"satoshi was an agorist"}"""
    }
  }
}
