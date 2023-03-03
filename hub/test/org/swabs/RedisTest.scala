package org.swabs

import cats.effect.IO
import cats.effect.IOApp
import org.swabs.core.models.UserModels.History
import org.swabs.core.models.UserModels.User
import org.swabs.core.redis.models.RedisKeys
import org.swabs.core.redis.{Client => RedisClient}
import play.api.libs.json.Json

object RedisTest extends IOApp.Simple {
  override def run: IO[Unit] =
    for {
      config   <- Config.singleClusterRedisConfig
      client    = RedisClient(config)
      user      = User("id", History(Map(RedisKeys.HISTORY -> "b")))
      _        <- client.update(user.token, Json.stringify(Json.toJson(user.history)))
      user0    <- client.lookup(user.token)
      _        <- user0 match {
        case Some(user) => IO.println(user)
        case None => IO.unit
      }
    } yield ()
}
