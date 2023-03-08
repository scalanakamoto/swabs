package org.swabs.core.models.user.errors

import cats.implicits.showInterpolator
import org.swabs.core.models.user.UserId

final case class UserNotFoundException(userId: UserId) extends Exception {
  override def getMessage: String = show"user of user id $userId not found"
}
