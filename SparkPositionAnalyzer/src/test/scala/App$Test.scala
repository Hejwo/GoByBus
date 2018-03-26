import java.io.InputStream
import java.time.Duration
import java.time.format.DateTimeFormatter

import geometry.RichGeometry._
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FlatSpec, GivenWhenThen}

@RunWith(classOf[JUnitRunner])
class App$Test extends FlatSpec with BeforeAndAfter with GivenWhenThen {

  private val master = "local[2]"
  private val appName = "testApp"

  private var sparkContext: SparkContext = _
  private var lines: Seq[String] = _

  before {
    val conf = new SparkConf()
      .setMaster(master)
      .setAppName(appName)

    sparkContext = new SparkContext(conf)
    sparkContext.setLogLevel("WARN")
    lines = readFile("line.csv")
  }

  it should "map rows to BusPosition classes" in {
    val rdd = sparkContext.parallelize(lines)

    val busPositions = App.mapToBusPositions(rdd)

    assert(busPositions.count() == 4341)
  }

  it should "find common date" in {
    val rdd = sparkContext.parallelize(lines)

    val commonDay = App.findCommonDay(rdd)

    val result = commonDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    assert(result == "2016-08-12T00:00:00")
  }

  it should "find longest brigade sorted" in {
    val rdd = sparkContext.parallelize(lines)

    val longestBrigadeDayDataSorted = App.findLongestBrigadeDayDataSorted(rdd)

    assert(longestBrigadeDayDataSorted.count() > 0)
    assert(longestBrigadeDayDataSorted.map(_.brigade).first().equalsIgnoreCase("35"))
  }

  it should "find longest brigade" in {
    val rdd = sparkContext.parallelize(lines)

    val longestBrigade = App.findLongestBrigade(rdd)

    assert(longestBrigade.equals("35"))
  }

  it should "pair" in {
    val rdd = sparkContext.parallelize(lines)
    val longestBrigadeDayDataSorted = App.findLongestBrigadeDayDataSorted(rdd)

    val busFrames = App.findBusFrames(longestBrigadeDayDataSorted)
//    assert(busFrames.count() == 437)

    val longestDuration = busFrames.
      map(frame => (frame, Duration.between(frame.time1.toLocalDateTime, frame.time2.toLocalDateTime).getSeconds)).
      sortBy(_._2, false).
      first()

    println(longestDuration._1.position1)
    println(longestDuration._1.position2)

    val smallestDistance = busFrames.
      map(frame => (frame, frame.position1.distance(frame.position2))).
      sortBy(_._2).take(10).foreach(println)
  }


  def readFile(fileName: String): Seq[String] = {
    val stream: InputStream = getClass.getResourceAsStream(fileName)
    val source = scala.io.Source.fromInputStream(stream)
    source.getLines().toSeq
  }

  after {
    if (sparkContext != null) sparkContext.stop()
  }

}