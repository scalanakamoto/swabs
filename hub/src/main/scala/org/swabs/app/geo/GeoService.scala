package org.swabs.app.geo

import cats.effect.IO
import cats.implicits._
import com.google.protobuf.Empty
import dev.profunktor.redis4cats.effects.Distance
import dev.profunktor.redis4cats.effects.GeoRadius
import io.lettuce.core.GeoArgs
import org.swabs.app.ServiceEngine
import org.swabs.app.geo.models.GeoUnit._
import org.swabs.app.geo.models.LookupRadiusRequest
import org.swabs.app.geo.models.UserGeoLocationRequest
import org.swabs.app.geo.models.UserGeoRadiusResponse

private[app] object GeoService extends ServiceEngine.RedisEngine {
  def setPosition(request: UserGeoLocationRequest): IO[Empty] =
    redisClient.flatMap(_.setLocation(locationHashCode, request.asRedisGeoLocation)) *> IO.pure(Empty)

  def lookupRadius(request: LookupRadiusRequest): IO[List[UserGeoRadiusResponse]] =
    setPosition(request.userGeoLocation) *> redisClient.flatMap { client =>
      for {
        userId      <- IO.pure(request.userGeoLocation.userId.show)

        distance     = Distance(request.distance)
        geoLocation  = request.userGeoLocation.asRedisGeoLocation
        geoRadius    = GeoRadius(geoLocation.lon, geoLocation.lat, distance)

        results     <- client.geoRadius(locationHashCode, geoRadius, request.unit.toGeoArgsUnit)
        candidates  <- results
                        .filter(_.value != userId)
                        .traverse(c => client.geoDist(locationHashCode, userId, c.value, GeoArgs.Unit.m).map(c -> _))
                        .map(_.flatMap { case (candidate, distance) => UserGeoRadiusResponse.from(candidate, distance) })
      } yield candidates
    }
}
