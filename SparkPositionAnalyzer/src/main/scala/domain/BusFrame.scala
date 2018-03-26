package domain

import java.sql.Timestamp

import com.esri.core.geometry.Point

case class BusFrame(status: String, line: String, time1: Timestamp, time2: Timestamp, position1: Point,
                    position2: Point, brigade: String, lowFloor: Boolean) extends Serializable