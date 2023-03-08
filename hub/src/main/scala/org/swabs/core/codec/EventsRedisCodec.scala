package org.swabs.core.codec

import dev.profunktor.redis4cats.data.{RedisCodec => Redis4CatsCodec}
import io.lettuce.core.codec.RedisCodec
import org.swabs.core.models.user.events.Events
import org.swabs.core.redis.models.ParsingException
import play.api.libs.json.Json

import java.nio.ByteBuffer

final class EventsRedisCodec extends RedisCodec[String, Events] {
  override def decodeKey(bytes: ByteBuffer): String = Json.stringify(Json.parse(bytes.array()))

  override def decodeValue(bytes: ByteBuffer): Events = {
    Json.parse(bytes.array()).asOpt[Events] match {
      case Some(events) => events
      case None => throw ParsingException("events could not parsed.")
    }
  }

  override def encodeKey(key: String): ByteBuffer = ???

  override def encodeValue(value: Events): ByteBuffer = ???
}

object EventsRedisCodec {
  implicit val codec: Redis4CatsCodec[String, Events] = Redis4CatsCodec(new EventsRedisCodec)
}
