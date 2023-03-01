package org.swabs.core.redis

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.showInterpolator
import dev.profunktor.redis4cats._
import dev.profunktor.redis4cats.algebra.StringCommands
import dev.profunktor.redis4cats.connection.RedisClient
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log.Stdout._
import org.swabs.core.redis.Client.KeyNotFoundException
import org.swabs.core.redis.Client.RedisConfig
import org.swabs.core.redis.Client.SignupException
import org.typelevel.log4cats.{Logger => CatsLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

final case class Client(config: RedisConfig) {
  private val logger: CatsLogger[IO] = Slf4jLogger.getLogger[IO]

  private val api: Resource[IO, StringCommands[IO, String, String]] =
    RedisClient[IO]
      .from(config.uri)
      .flatMap(Redis[IO].fromClient(_, RedisCodec.Utf8))

  def lookup(key: String): IO[Option[String]] = api.use(_.get(key))

  def signup(key: String, value: String): IO[Unit] =
    update(key, value).handleErrorWith {
      case _@KeyNotFoundException =>
        logger.error(SignupException)(SignupException.getMessage)
      case ex =>
        logger.error(ex)(ex.getMessage)
    }

  def update(key: String, value: String): IO[Unit] =
    for {
      entry <- lookup(key)
      _     <- IO.whenA(entry.nonEmpty)(IO.raiseError(KeyNotFoundException))
      _     <- api.use(_.set(key, value)) <* logger.debug(show"updated key $key")
    } yield ()
}

object Client {
  final case class RedisConfig(uri: String, port: Option[Int])

  case object SignupException extends Exception {
    override def getMessage: String = "entity is already signed up"
  }

  case object KeyNotFoundException extends Exception {
    override def getMessage: String = "key not found"
  }
}
