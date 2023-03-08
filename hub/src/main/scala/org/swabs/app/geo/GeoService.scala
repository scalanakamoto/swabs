package org.swabs.app.geo

import cats.effect.IO
import cats.implicits.toShow
import dev.profunktor.redis4cats.effects.Distance
import dev.profunktor.redis4cats.effects.GeoRadius
import org.swabs.app.ServiceEngine
import org.swabs.app.geo.models.GeoUnit._
import org.swabs.app.geo.models.LookupRadiusRequest
import org.swabs.app.geo.models.UserGeoLocationRequest
import org.swabs.app.geo.models.UserGeoRadiusResponse

private[app] object GeoService extends ServiceEngine.RedisEngine {
  def setPosition(request: UserGeoLocationRequest): IO[Unit] =
    redisClient.flatMap(_.setLocation(locationHashCode, request.asRedisGeoLocation))

  def lookupRadius(request: LookupRadiusRequest): IO[List[UserGeoRadiusResponse]] =
    redisClient.flatMap { client =>
      for {
        _           <- setPosition(request.userGeoLocation)

        distance    <- IO.pure(Distance(request.distance))
        geoLocation  = request.userGeoLocation.asRedisGeoLocation
        geoRadius    = GeoRadius(geoLocation.lon, geoLocation.lat, distance)

        results     <- client.geoRadius(locationHashCode, geoRadius, request.unit.toGeoArgsUnit)

        candidates   = results.filter(_.value != request.userGeoLocation.userId.show).flatMap(UserGeoRadiusResponse.from)
      } yield candidates
    }
}
