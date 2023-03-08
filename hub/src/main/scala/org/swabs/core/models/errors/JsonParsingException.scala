package org.swabs.core.models.errors

final case class JsonParsingException(message: String) extends Exception {
  override def getMessage: String = s"could not parse $message"
}
