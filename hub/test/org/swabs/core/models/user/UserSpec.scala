package org.swabs.core.models.user

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.events.Events
import org.swabs.core.models.user.events.SignUp
import org.swabs.core.models.user.events.Transactions.Note
import org.swabs.core.models.user.events.Transactions.Transaction
import org.swabs.core.models.user.events.Transactions.TransactionAmount
import org.swabs.core.models.user.events.Transactions.TransactionDateTime
import org.swabs.util.GlobalDateTimeFormat
import play.api.libs.json.Json

import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

class UserSpec extends AnyWordSpec with Matchers {

  private val now = LocalDateTime.now(Clock.systemUTC()).format(GlobalDateTimeFormat.apply)
  private val userId = UserId(UUID.fromString("f042f433-496f-484e-958f-b8cdd77e622f"))
  private val transactions = List(Transaction(
    dateTime = TransactionDateTime(LocalDateTime.parse(now)),
    amount = TransactionAmount(123.0),
    currency = Currency.SATS,
    note = Note("satoshi nakamoto is a genius")
  ))
  private val jsonStr = s"""{"userId":"f042f433-496f-484e-958f-b8cdd77e622f","events":{"signUp":"$now","transactions":[{"dateTime":"$now","amount":123,"currency":"SATS","note":"satoshi nakamoto is a genius"}]}}"""

  "User#writes" must {
    "work" in {
      val json = Json.toJson(User(userId, Events(SignUp(now), transactions)))
      Json.stringify(json) mustBe jsonStr
    }
  }

  "User#reads" must {
    "work" in {
      Json.parse(jsonStr).asOpt[User] mustBe Some(User(userId, Events(SignUp(now), transactions)))
    }
  }
}
