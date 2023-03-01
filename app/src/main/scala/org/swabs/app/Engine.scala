package org.swabs.app

import cats.effect.IO
import org.swabs.Config
import org.swabs.core.redis.{Client => RedisClient}

import java.time.Clock

trait Engine {
  implicit val clock: Clock = Clock.systemUTC()
}

object Engine {
  trait RedisEngine extends Engine {
    val redisClient: IO[RedisClient] = Config.redisSingleClusterConfig.map(RedisClient(_))
  }
}
