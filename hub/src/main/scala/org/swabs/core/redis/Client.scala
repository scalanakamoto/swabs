package org.swabs.core.redis

import cats.effect.IO
import cats.effect.Resource
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effect.Log.Stdout._
import dev.profunktor.redis4cats.effects
import dev.profunktor.redis4cats.effects.GeoLocation
import dev.profunktor.redis4cats.effects.GeoRadius
import io.lettuce.core.GeoArgs
import org.swabs.core.redis.models.KeyNotFoundException
import org.swabs.core.redis.models.SignupException
import org.swabs.core.redis.models.UpdateException
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger => CatsLogger}

final case class Client(config: RedisConfig) {
  private val logger: CatsLogger[IO] = Slf4jLogger.getLogger[IO]

  private val api: Resource[IO, RedisCommands[IO, String, String]] = Redis[IO].utf8(config.uri)

  def lookup(hashCode: String, key: String): IO[String] =
    api.use { cmd =>
      for {
        valueStrOps <- cmd.hGet(hashCode, key)
        values      <- IO.fromOption(valueStrOps)(KeyNotFoundException(key))
      } yield values
    }

  def geoRadius(hashCode: String, radius: GeoRadius, unit: GeoArgs.Unit): IO[List[effects.GeoRadiusResult[String]]] =
    api.use(_.geoRadius(hashCode, radius, unit, GeoArgs.Builder.coordinates()))

  def geoDist(hashCode: String, from: String, candidate: String, unit: GeoArgs.Unit): IO[Double] =
    api.use(_.geoDist(hashCode, from, candidate, unit))

  def setLocation(hashCode: String, locations: GeoLocation[String]): IO[Unit] = api.use(_.geoAdd(hashCode, locations))

  def signup(hashCode: String, key: String, field: String): IO[Unit] =
    (for {
      isEntity <- api.use(_.hGet(hashCode, key))
      _        <- IO.whenA(isEntity.isEmpty)(IO.raiseError(SignupException))
      _        <- update(hashCode, key, field)
    } yield ()).handleErrorWith {
      case _: UpdateException =>
        logger.error(SignupException)(SignupException.getMessage)
      case ex =>
        logger.error(ex)(ex.getMessage)
    }

  def update(hashCode: String, key: String, field: String): IO[Unit] =
    api.use { cmd =>
      for {
        isSuccessfully <- cmd.hSet(hashCode, key, field)
        _              <- IO.unlessA(isSuccessfully)(IO.raiseError(UpdateException(s"could not update by key $key")))
      } yield ()
    }
}
