package org.swabs.core.redis.models

import play.api.libs.json.Writes

object RedisKeys extends Enumeration {
  type RedisKeys = Value

  val SIGNUP: RedisKeys.Value = Value("signUp")
  val HISTORY: RedisKeys.Value = Value("history")

  implicit val writes: Writes[RedisKeys] = Writes.enumNameWrites
}
