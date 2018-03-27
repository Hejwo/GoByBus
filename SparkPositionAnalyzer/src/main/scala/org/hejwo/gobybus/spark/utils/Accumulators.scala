package org.hejwo.gobybus.spark.utils

import java.sql.Timestamp
import java.time.LocalDate

import org.hejwo.gobybus.spark.domain.domain.BusFrame

object Accumulators {

  def minBusFrameAcc(day: LocalDate, line: String, brigade: String) = {
    val zeroStamp = Timestamp.valueOf(day.atStartOfDay())
    import Double.{MinValue => dMin}
    import Long.{MinValue => lMin}
    BusFrame("ACC-EMPTY", line, brigade, zeroStamp, zeroStamp, (dMin, dMin), (dMin, dMin), lMin, dMin)
  }

  def maxBusFrameAcc(day: LocalDate, line: String, brigade: String) = {
    val zeroStamp = Timestamp.valueOf(day.atStartOfDay())
    import Double.{MaxValue => dMax}
    import Long.{MaxValue => lMax}
    BusFrame("ACC-EMPTY", line, brigade, zeroStamp, zeroStamp, (dMax, dMax), (dMax, dMax), lMax, dMax)
  }

  def maxDistance(): (BusFrame, BusFrame) => BusFrame = (f1, f2) => if (f1.distance > f2.distance) f1 else f2

  def minDistance(): (BusFrame, BusFrame) => BusFrame = (f1, f2) => if (f1.distance < f2.distance) f1 else f2


}
