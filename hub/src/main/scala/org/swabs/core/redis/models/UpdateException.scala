package org.swabs.core.redis.models

final case class UpdateException(message: String) extends Exception {
  override def getMessage: String = message
}
