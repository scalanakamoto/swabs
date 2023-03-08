package org.swabs.app.geo

import cats.effect.IO
import dev.profunktor.redis4cats.effects.Distance
import dev.profunktor.redis4cats.effects.GeoRadius
import org.swabs.app.ServiceEngine
import org.swabs.app.geo.models.GeoUnit._
import org.swabs.app.geo.models.LookupRadiusRequest
import org.swabs.app.geo.models.UserGeoLocation
import org.swabs.app.geo.models.UserGeoRadiusResult

private[app] object GeoService extends ServiceEngine.RedisEngine {
  def setPosition(request: UserGeoLocation): IO[Unit] =
    redisClient.flatMap(_.setLocation(locationHashCode, request.toRedisGeoLocation))

  def lookupRadius(request: LookupRadiusRequest): IO[List[UserGeoRadiusResult]] =
    redisClient.flatMap { client =>
      for {
        distance    <- IO.pure(Distance(request.distance))
        geoLocation  = request.geoLocation.toRedisGeoLocation
        unit         = request.unit.toGeoArgsUnit
        geoRadius    = GeoRadius(geoLocation.lon, geoLocation.lat, distance)

        locations   <- client.geoRadius(locationHashCode, geoRadius, unit).map(_.flatMap(UserGeoRadiusResult.from))
      } yield locations
    }
}
