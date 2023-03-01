package org.swabs

import cats.effect.IO
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.comcast.ip4s.Host
import com.comcast.ip4s.IpLiteralSyntax
import com.comcast.ip4s.Port
import com.typesafe.config.ConfigFactory
import com.typesafe.config.{Config => TypeSafeConfig}
import org.swabs.Server.ServerConfig
import org.swabs.core.redis.Client.RedisConfig

sealed trait Config {

  val config: IO[TypeSafeConfig] = IO(ConfigFactory.load())

  val serverUrl: IO[String] = config.map(_.getString("server.url"))
  val serverPort: IO[Int] = config.map(_.getInt("server.port"))

  val redisSingleClusterUri: IO[String] =
    config.map(config => s"redis://${config.getString("redis.single-node-address")}")
}

object Config extends Config {
  val redisSingleClusterConfig: IO[RedisConfig] = redisSingleClusterUri.map(RedisConfig(_, None))
  val serverURI: IO[ServerConfig] = {
    (serverUrl.map(Host.fromString), serverPort.map(Port.fromInt)).mapN {
      case (Some(host), Some(port)) => ServerConfig(host, port)
      case _ => ServerConfig(ipv4"0.0.0.0", port"8443")
    }
  }
}
