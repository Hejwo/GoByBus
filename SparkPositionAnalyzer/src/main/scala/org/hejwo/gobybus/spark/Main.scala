package org.hejwo.gobybus.spark

import java.time.LocalDate

import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.{Dataset, SparkSession}
import org.hejwo.gobybus.spark.config.SparkConfig._
import org.hejwo.gobybus.spark.domain.domain.{BusFrame, BusPosition}
import org.hejwo.gobybus.spark.utils.{Accumulators, GeometryUtils, TimeUtils}

object Main {

  def main(args: Array[String]): Unit = {
    implicit val spark = createSparkSession
    import spark.implicits._

    val busPositions = MongoSpark.load(spark).as[BusPosition]
  }

  /*
    On small scale it look good to find terminus, but for larger dump shows weird output
   */
  def findFarthestFrame(busPositions: Dataset[BusPosition])
                       (day: LocalDate, line: String, brigade: String)(implicit spark: SparkSession) = {
    val filtered = findPositionsForADay(busPositions, day, pos => pos.firstLine == line && pos.brigade == brigade)
    val framed = joinIntoFrames(filtered)
    framed.rdd.fold(Accumulators.minBusFrameAcc(day, line, brigade))(Accumulators.maxDistance())
  }

  private def createSparkSession: SparkSession = {
    val spark = SparkSession.builder()
      .master(sparkMaster)
      .appName(sparkAppName)
      .config("spark.mongodb.input.uri", s"mongodb://$mongoUrl/$mongoLocationsDatabase.$mongoLocationsCollection")
      .getOrCreate()
    spark
  }

  private def printDetailedFrame(frame: BusFrame) {
    println(frame)
    import frame._
    val details1 = s"line: $line, brigade: $brigade, time1: $time1, span: $secondsSpan, distance: $distance"
    val details2 = s"line: $line, brigade: $brigade, time1: $time2, span: $secondsSpan, distance: $distance"
    println("\t{ \"lat\": \"" + point1._1 + "\", \"lng\": \"" + point1._2 + "\", \"details\": \"" + details1 + "\"}")
    println("\t{ \"lat\": \"" + point2._1 + "\", \"lng\": \"" + point2._2 + "\", \"details\": \"" + details2 + "\"}")
  }

  def findPositionsForADay(busPositions: Dataset[BusPosition],
                           day: LocalDate,
                           additionalFilter: BusPosition => Boolean = _ => true): Dataset[BusPosition] = {
    busPositions.filter { pos =>
      val dayStartDate = pos.time.toLocalDateTime.toLocalDate.atStartOfDay()
      dayStartDate == day.atStartOfDay()
    }.filter(additionalFilter)
  }

  def joinIntoFrames(busPositions: Dataset[BusPosition])(implicit spark: SparkSession): Dataset[BusFrame] = {
    import org.apache.spark.mllib.rdd.RDDFunctions._
    import spark.implicits._

    busPositions.orderBy("time").rdd.sliding(2).map {
      case Array(
      BusPosition(status1, firstLine1, brigade1, _, latitude1, longitude1, time1, _),
      BusPosition(status2, firstLine2, brigade2, _, latitude2, longitude2, time2, _)
      ) if firstLine1 == firstLine2 && brigade1 == brigade2 && time1 != time2
      =>
        val point1 = (latitude1, longitude1)
        val point2 = (latitude2, longitude2)
        val distance = GeometryUtils.calculateDistance(point1, point2)
        val secondsSpan = TimeUtils.secondsBetween(time1, time2)

        BusFrame(status1, firstLine1, brigade1, time1, time2, point1, point2, secondsSpan, distance)
    }.toDS()
  }


}
