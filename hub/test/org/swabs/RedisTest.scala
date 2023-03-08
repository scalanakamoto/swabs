package org.swabs

import cats.effect.IO
import cats.effect.IOApp
import cats.implicits.toShow
import cats.implicits.toTraverseOps
import dev.profunktor.redis4cats.effects
import dev.profunktor.redis4cats.effects.Distance
import dev.profunktor.redis4cats.effects.GeoLocation
import dev.profunktor.redis4cats.effects.GeoRadius
import dev.profunktor.redis4cats.effects.Latitude
import dev.profunktor.redis4cats.effects.Longitude
import io.lettuce.core.GeoArgs
import org.swabs.core.models.user.Currency
import org.swabs.core.models.user.User
import org.swabs.core.models.user.UserId
import org.swabs.core.models.user.events.Events
import org.swabs.core.models.user.events.Transactions.Note
import org.swabs.core.models.user.events.Transactions.Transaction
import org.swabs.core.models.user.events.Transactions.TransactionAmount
import org.swabs.core.models.user.events.Transactions.TransactionDateTime
import org.swabs.core.models.user.events.{SignUp => CoreSignUp}
import org.swabs.core.redis.{Client => RedisClient}
import play.api.libs.json.Json

import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

object RedisTest extends IOApp.Simple {
  private implicit val clock: Clock = Clock.systemUTC()
  private val userHashCode = "users"
  private val locationHashCode = "locations"

  override def run: IO[Unit] =
    for {
      config <- Config.singleClusterRedisConfig
      client  = RedisClient(config)

      _      <- IO.println("user test")
      user   <- userTest(client)
      _      <- IO.println(user)

      _      <- IO.println("geo test")
      found  <- geoTest(user, client)
      _      <- IO.println(found)
    } yield ()

  private def geoTest(user: User, client: RedisClient): IO[List[effects.GeoRadiusResult[String]]] = {
    val nextUser1UUID = "d4b1a0f0-dbb8-4aa4-a02d-98a55fbfd604"
    val nextUser2UUID = "e201c85d-4f54-44f6-9fae-8678bedc97b4"
    val nextUser1 = GeoLocation(Longitude(59.90975646224532), Latitude(10.740845013978463), nextUser1UUID)
    val nextUser2 = GeoLocation(Longitude(59.72692829132007), Latitude(10.051084710600867), nextUser2UUID)

    for {
      long      <- IO.pure(Longitude(59.91400139002258))
      lat       <- IO.pure(Latitude(10.745020273403547))
      geoUser   <- IO(GeoLocation(long, lat, user.userId.show))
      _         <- client.setLocation(locationHashCode, geoUser)

      _         <- client.setLocation(locationHashCode, nextUser1)
      _         <- client.setLocation(locationHashCode, nextUser2)

      geoRadius  = GeoRadius(long, lat, Distance(1000.0))
      radius    <- client.geoRadius(locationHashCode, geoRadius, GeoArgs.Unit.m)
      found     <- radius.traverse(IO.pure).map(_.filter(_.value != user.userId.show))
    } yield found
  }

  private def userTest(client: RedisClient): IO[User] =
    for {
      userid       <- IO(UserId(UUID.fromString("f042f433-496f-484e-958f-b8cdd77e622f")))
      transactions  = List(Transaction(
                        dateTime = TransactionDateTime(LocalDateTime.now(clock)),
                        amount   = TransactionAmount(123.0),
                        currency = Currency.SATS,
                        note     = Note("satoshi nakamoto is a genius")
                      ))
      user          = User(userid, Events(CoreSignUp.fromClock, transactions))
      _            <- client
                        .update(userHashCode, user.userId.show, Json.stringify(Json.toJson(user)))
                        .handleErrorWith(_ => IO.unit)
      user0        <- client.lookup(userHashCode, user.userId.show)
      found        <- IO.fromOption(Json.parse(user0).asOpt[User])(new Throwable("parse error"))
    } yield found
}
