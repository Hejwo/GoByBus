package utils

import java.time.LocalDateTime

object TimeCompare extends Ordering[LocalDateTime] {
  override def compare(x: LocalDateTime, y: LocalDateTime): Int = x.compareTo(y)
}
