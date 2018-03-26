import java.io.InputStream
import java.time.temporal.ChronoUnit

import domain.TramPosition._
import domain.{TramFrame, TramPosition, TramPositionRaw}
import geometry.RichGeometry._
import org.apache.spark.mllib.rdd.RDDFunctions._
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import utils.TimeCompare

object PositionApp {
  implicit val formats = DefaultFormats
  implicit val timeCompare = TimeCompare

  def main(args: Array[String]) {
    val sc: SparkContext = createSparkContext

    val positionObj = readPositions("points.csv")
    val positionsRdd = sc.parallelize(positionObj)

    val cachedFrames = positionsRdd
      .filter(byLineAndPosition("22", "11"))
      .sortBy(_.time)
      .sliding(2)
      .map {
        case Array(t1: TramPosition, t2: TramPosition) =>
          val secondsSpan = t1.time.until(t2.time, ChronoUnit.SECONDS)
          val distSpanMeters = t1.point.distance(t2.point) * 100 * 1000
          val avgSpeed = (distSpanMeters / 1000) / (secondsSpan * 0.000277778)
          TramFrame(t1, t2, t2.point.getX, t2.point.getY, distSpanMeters, secondsSpan, avgSpeed)
      }
      .cache()


    val lowSpeedTrams = cachedFrames.filter(_.avgSpeedKmH == 0)

        .foreach(println)
//      .map { frame =>
//        val vector = Vectors.dense(frame.tramPosition1.point.getX, frame.tramPosition1.point.getY)
//        vector
//      }.cache()

//     val clusters = KMeans.train(lowSpeedTrams, 4, 20)
//     clusters.clusterCenters.foreach(cl =>
//       println("{\"x\": "+cl(0)+ ", \"y\": "+cl(1)+"}")
//     )
  }

  def readPositions(filename: String): Seq[TramPosition] = {
    readFileLines(filename).map(str => str.replace("{{", "{").replace("}}", "}"))
      .map(json => convertRaw(parse(json).extract[TramPositionRaw]))
  }

  def readFileLines(fileName: String): Seq[String] = {
    val stream: InputStream = getClass.getResourceAsStream(fileName)
    val source = scala.io.Source.fromInputStream(stream)
    source.getLines().toSeq
  }

  private def createSparkContext = {
    val sparkConfiguration = new SparkConf().setAppName("test1").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparkConfiguration)
    sc.setLogLevel("WARN")
    sc
  }

}
