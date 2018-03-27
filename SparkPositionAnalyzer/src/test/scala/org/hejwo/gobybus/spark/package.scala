package org.hejwo.gobybus

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.hejwo.gobybus.spark.domain.domain._

package object spark {

  def lineToBusStop(line: String): Option[BusPosition] = {
    try {
      val fields = line.split(',')
      val loc = BusPosition(status = fields(0),
        firstLine = fields(1), brigade = fields(7),
        lines = List(fields(3)),
        longitude = fields(2).toDouble, latitude = fields(5).toDouble,
        time = toTimestamp(fields(4)), lowFloor = fields(6).toBoolean)
      Some(loc)
    } catch {
      case _ => None
    }
  }

  def toTimestamp(rawTime: String): Timestamp = {
    val localDateTime = LocalDateTime.parse(rawTime, DateTimeFormatter.ISO_DATE_TIME)
    Timestamp.valueOf(localDateTime)
  }

}
