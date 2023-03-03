package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.events.Transactions.TransactionDateTime
import org.swabs.util.GlobalDateTimeFormat
import play.api.libs.json.Json

import java.time.LocalDateTime

class TransactionDateTimeSpec extends AnyWordSpec with Matchers {
  "TransactionDateTime#reads" must {
    "work" in {
      val ldt = LocalDateTime.parse("2023-03-07T15:35:33", GlobalDateTimeFormat.apply)
      Json.parse(""""2023-03-07T15:35:33"""").asOpt[TransactionDateTime] mustBe Some(TransactionDateTime(ldt))
    }
  }

  "TransactionDateTime#writes" must {
    "work" in {
      val ldt = LocalDateTime.parse("2023-03-07T15:35:33", GlobalDateTimeFormat.apply)
      Json.stringify(Json.toJson(TransactionDateTime(ldt))) mustBe """"2023-03-07T15:35:33""""
    }
  }

  "TransactionDateTime#parse" must {
    "work" in {
      TransactionDateTime.parse.parse("2023-03-07T15:35:33").toOption.nonEmpty mustBe true
    }
    "fail" in {
      TransactionDateTime.parse.parse("2023-03-07 15:35:33").toOption.isEmpty mustBe true
      TransactionDateTime.parse.parse("2023-03-07 15:35:33.000").toOption.isEmpty mustBe true
      TransactionDateTime.parse.parse("2023-03-07 15:35:33.000Z").toOption.isEmpty mustBe true
      TransactionDateTime.parse.parse("07-03-2003 15:35:33.000Z").toOption.isEmpty mustBe true
    }
  }
}
