package org.swabs.core.models

import org.swabs.core.models
import play.api.libs.json.Writes

object RedisKeys extends Enumeration {
  type RedisKeys = Value

  val SIGNUP: models.RedisKeys.Value = Value("signUp")
  val HISTORY: models.RedisKeys.Value = Value("history")

  implicit val writes: Writes[RedisKeys] = Writes.enumNameWrites
}
