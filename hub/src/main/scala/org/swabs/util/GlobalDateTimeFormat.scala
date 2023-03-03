package org.swabs.util

import java.time.format.DateTimeFormatter

object GlobalDateTimeFormat {
  private val pattern = "yyyy-MM-dd'T'HH:mm:ss"
  def apply: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
}
