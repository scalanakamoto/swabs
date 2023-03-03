package org.swabs.app

import cats.effect.IO
import org.swabs.Config
import org.swabs.core.redis.{Client => RedisClient}

import java.time.Clock

private[app] trait ServiceEngine {
  implicit val clock: Clock  = Clock.systemUTC()
  final val userHashCode     = "users"
  final val locationHashCode = "locations"
}

private[app] object ServiceEngine {
  trait RedisEngine extends ServiceEngine {
    val redisClient: IO[RedisClient] = Config.singleClusterRedisConfig.map(RedisClient)
  }
}
