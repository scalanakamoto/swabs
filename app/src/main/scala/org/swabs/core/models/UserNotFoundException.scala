package org.swabs.core.models

import cats.implicits.showInterpolator

final case class UserNotFoundException(token: UserToken) extends Exception {
  override def getMessage: String = show"user with token $token not found"
}
