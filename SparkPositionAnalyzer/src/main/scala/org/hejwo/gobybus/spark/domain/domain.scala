package org.hejwo.gobybus.spark.domain

import java.sql.Timestamp

object domain {

  case class BusPosition(status: String,
                         firstLine: String,
                         brigade: String,
                         lines: List[String],
                         latitude: Double, longitude: Double,
                         time: Timestamp,
                         lowFloor: Boolean) extends Serializable

  case class BusFrame(status: String,
                      line: String, brigade: String,
                      time1: Timestamp, time2: Timestamp,
                      point1: (Double, Double),
                      point2: (Double, Double),
                      secondsSpan: Long,
                      distance: Double) extends Serializable

}
