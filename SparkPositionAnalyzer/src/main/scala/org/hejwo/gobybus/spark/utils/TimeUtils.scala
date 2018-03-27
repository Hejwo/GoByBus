package org.hejwo.gobybus.spark.utils

import java.sql.Timestamp
import java.time.temporal.ChronoUnit.SECONDS

object TimeUtils {

  def secondsBetween(time1: Timestamp, time2: Timestamp) = SECONDS.between(time1.toLocalDateTime, time2.toLocalDateTime)

}
