package org.swabs.core.redis.models

final case class KeyNotFoundException(key: String) extends Exception {
  override def getMessage: String = s"key $key not found"
}
