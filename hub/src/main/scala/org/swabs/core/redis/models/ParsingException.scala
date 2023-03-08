package org.swabs.core.redis.models

final case class ParsingException(message: String) extends Exception {
  override def getMessage: String = message
}
