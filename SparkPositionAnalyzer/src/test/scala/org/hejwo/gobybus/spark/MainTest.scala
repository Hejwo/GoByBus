package org.hejwo.gobybus.spark

import java.io.InputStream
import java.time.LocalDate

import org.apache.spark.sql.Dataset
import org.hejwo.gobybus.spark.domain.domain.{BusFrame, BusPosition}
import org.scalatest.Matchers

class MainTest extends SparkTest with Matchers {

  import spark.implicits._
  val positionsRdd = spark.sparkContext
    .parallelize(readFile("/line-9-shortDump.csv"))
    .flatMap(lineToBusStop).toDS()

  "spark" should "load 4341 lines from file" in {
    val positionsCount = positionsRdd.count()
    assert(positionsCount === 4341)
  }

  "spark" should "convert lines to Dataset" in {
    positionsRdd shouldBe a[Dataset[BusPosition]]
  }

  "main app" should "find NOT positions for 2016-08-13" in {
    val positionsForDay = Main.findPositionsForADay(positionsRdd, LocalDate.of(2016, 8, 13))
    val resultSample = positionsForDay.take(10)

    assert(resultSample.length === 0)
  }

  "main app" should "find 4341 positions for 2016-08-12" in {
    val positionsForDay = Main.findPositionsForADay(positionsRdd, LocalDate.of(2016, 8, 12))
    val resultCount = positionsForDay.count()

    assert(resultCount === 4341)
  }

  "main app" should "find positions for 2016-08-12, line 9, brigade 35" in {
    val additionalFilter: BusPosition => Boolean = pos => pos.firstLine == "9" && pos.brigade == "35"

    val positionsForDay = Main.findPositionsForADay(positionsRdd, LocalDate.of(2016, 8, 12), additionalFilter).collect()
    assert(positionsForDay.length === 438)
  }

  "main app" should "find create position frames for 2016-08-12, line 9, brigade 35" in {
    val additionalFilter: BusPosition => Boolean = pos => pos.firstLine == "9" && pos.brigade == "35"

    val positionsForDay = Main.findPositionsForADay(positionsRdd, LocalDate.of(2016, 8, 12), additionalFilter)
    val joinedFrames = Main.joinIntoFrames(positionsForDay).collect()

    assert(joinedFrames.length === 437)
  }

  "main app" should "find farthest bus frame for 2016-08-12, line 9, brigade 35" in {
    val farthestBusFrame = Main.findFarthestFrame(positionsRdd)(LocalDate.of(2016, 8, 12), "9", "35")

    val terminus1pos = farthestBusFrame.point1
    val terminus2pos = farthestBusFrame.point2

    assert(terminus1pos == (52.1758614, 20.9437542))
    assert(terminus2pos == (52.2439194, 21.0804253))
  }

  private def printDetailedFrame(frame: BusFrame) {
    println(frame)
    import frame._
    val details1 = s"line: $line, brigade: $brigade, time1: $time1, span: $secondsSpan, distance: $distance"
    val details2 = s"line: $line, brigade: $brigade, time1: $time2, span: $secondsSpan, distance: $distance"
    println("\t{ \"lat\": \"" + point1._1 + "\", \"lng\": \"" + point1._2 + "\", \"details\": \"" + details1 + "\"}")
    println("\t{ \"lat\": \"" + point2._1 + "\", \"lng\": \"" + point2._2 + "\", \"details\": \"" + details2 + "\"}")
  }

  def readFile(fileName: String): Seq[String] = {
    val stream: InputStream = getClass.getResourceAsStream(fileName)
    val source = scala.io.Source.fromInputStream(stream)
    source.getLines().toSeq
  }

}
