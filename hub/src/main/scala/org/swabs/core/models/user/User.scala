package org.swabs.core.models.user

import org.swabs.core.models.user.events.Events
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

final case class User(userId: UserId, events: Events) {
  def update(newEvents: Events): User = User(userId, events.combine(newEvents))
  def toJsonString: String = Json.stringify(Json.toJson(this))
}

object User {
  implicit val writes: Writes[User] = Json.writes[User]
  implicit val reads: Reads[User] = (
    (JsPath \ "userId").read[UserId] and
    (JsPath \ "events").read[Events]
  )(User.apply _)
}
