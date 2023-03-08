package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.events.Transactions.TransactionAmount

class TransactionAmountSpec extends AnyWordSpec with Matchers {
  "TransactionAmount#parse" must {
    "work" in {
      TransactionAmount.parse.parse("123.0123124").toOption.nonEmpty mustBe true
      TransactionAmount.parse.parse("2100000000000000").toOption.nonEmpty mustBe true
      TransactionAmount.parse.parse("2100000000000000.12345678").toOption.nonEmpty mustBe true

      TransactionAmount.parse.parse("123.123456789").toOption mustBe
        Some(TransactionAmount(BigDecimal("123.12345679")))
      TransactionAmount.parse.parse("2100000000000000.123456789").toOption mustBe
        Some(TransactionAmount(BigDecimal("2100000000000000.12345679")))
    }
  }
}
