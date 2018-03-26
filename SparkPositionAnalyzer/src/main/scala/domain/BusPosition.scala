package domain

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.esri.core.geometry.Point


case class BusPosition(status: String, lineName: String, position: Point, time: Timestamp, brigade: String, lowFloor: Boolean) extends Serializable

object BusPosition {

  def apply(line: String): Option[BusPosition] = {
    try {
      val fields = line.split(',')
      val loc = BusPosition(fields(0), fields(1), toPoint(fields(2), fields(5)), toDateTime(fields(4)), fields(7), fields(6).toBoolean)
      Some(loc)
    } catch {
      case _: Exception => None
    }
  }

  def toPoint(long: String, lat: String): Point = {
    new Point(long.toDouble, lat.toDouble)
  }

  def toDateTime(rawTime: String): Timestamp = {
    val localDateTime = LocalDateTime.parse(rawTime, DateTimeFormatter.ISO_DATE_TIME)
    Timestamp.valueOf(localDateTime)
  }


}
