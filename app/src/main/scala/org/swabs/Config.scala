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
  protected val config: IO[TypeSafeConfig] = IO(ConfigFactory.load())

  protected val serverUrl: IO[String] = config.map(_.getString("server.url"))
  protected val serverPort: IO[Int] = config.map(_.getInt("server.port"))

  protected val redisSingleClusterUri: IO[String] =
    config.map(config => s"redis://${config.getString("redis.single-node-address")}")

  protected val salt: IO[String] = config.map(_.getString("token.salt"))

  protected val jwtSecret: IO[String] = config.map(_.getString("jwt.secret"))
}

object Config extends Config {
  val singleClusterRedisConfig: IO[RedisConfig] = redisSingleClusterUri.map(RedisConfig(_, None))

  val serverConfig: IO[ServerConfig] = {
    (serverUrl.map(Host.fromString), serverPort.map(Port.fromInt)).mapN {
      case (Some(host), Some(port)) => ServerConfig(host, port)
      case _ => ServerConfig(ipv4"0.0.0.0", port"8443")
    }
  }

  val tokenSalt: IO[String] = salt.handleErrorWith(_ => IO.raiseError(ConfigEntryNotFoundException("token salt")))

  val secret: IO[String] = jwtSecret.handleErrorWith(_ => IO.raiseError(ConfigEntryNotFoundException("jwt secret")))

  private final case class ConfigEntryNotFoundException(entity: String) extends Exception {
    override def getMessage: String = s"could not found $entity in config"
  }
}
