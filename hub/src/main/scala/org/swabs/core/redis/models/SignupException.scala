package org.swabs.core.redis.models

case object SignupException extends Exception {
  override def getMessage: String = "entity is already signed up"
}
