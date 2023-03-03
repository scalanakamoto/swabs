package org.swabs.core.models

import org.swabs.core.redis.models.RedisKeys.RedisKeys
import play.api.libs.json.Writes

object UserModels {
  final case class User(token: String, history: History) {
    def update(newHistory: History): User = User(token, history.combine(newHistory))
  }

  final case class History(values: Map[RedisKeys, String]) {
    def combine(newHistory: History): History = History(values ++ newHistory.values)
  }

  object History {
    implicit val writes: Writes[History] = Writes.of[Map[RedisKeys, String]].contramap(_.values)
  }
}
