package org.swabs.core.models.user.events

import org.swabs.core.models.user.events.Transactions.Transaction
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json._

final case class Events(signUp: SignUp, transactions: List[Transaction]) {
  def combine(newEvent: Events): Events = Events(signUp, transactions ++ newEvent.transactions)
  def toJsonString: String = Json.prettyPrint(Json.toJson(this))
}

object Events {
  implicit val eventsFormat: Format[Events] = new Format[Events] {
    override def reads(json: JsValue): JsResult[Events] = {
      for {
        signUp       <- (json \ "signUp").validate[SignUp]
        transactions <- (json \ "transactions").validate[List[Transaction]]
      } yield Events(signUp, transactions)
    }

    override def writes(events: Events): JsValue = {
      Json.obj(
        "signUp" -> events.signUp,
        "transactions" -> Json.toJson(events.transactions)
      )
    }
  }
}
