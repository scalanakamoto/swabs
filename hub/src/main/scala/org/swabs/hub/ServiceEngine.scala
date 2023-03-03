package org.swabs.hub

import cats.effect.IO
import org.swabs.Config
import org.swabs.core.redis.{Client => RedisClient}

import java.time.Clock

private[hub] trait ServiceEngine {
  implicit val clock: Clock = Clock.systemUTC()
}

private[hub] object ServiceEngine {
  trait RedisEngine extends ServiceEngine {
    val redisClient: IO[RedisClient] = Config.singleClusterRedisConfig.map(RedisClient(_))
  }
}
