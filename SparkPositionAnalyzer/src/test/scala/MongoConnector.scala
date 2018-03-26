import java.sql.Timestamp

import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, GivenWhenThen}

case class BusPosition(status: String,
                       firstLine: String,
                       lines: List[String],
                       latitude: Double, longitude: Double,
                       time: Timestamp,
                       brigade: String,
                       lowFloor: Boolean) extends Serializable

class MongoConnector extends FlatSpec with BeforeAndAfter with GivenWhenThen {

  val spark = SparkSession.builder()
    .master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1:27017/locations.locationData")
    .getOrCreate()

  it should "connect" in {
    import spark.implicits._

    val busPositions = MongoSpark.load(spark).as[BusPosition]

    val linesAndBrigadesCount = busPositions
      .groupByKey(pos => (pos.firstLine, pos.brigade))
      .keys.groupByKey(_._1).count()
      .orderBy("count(1)")
      .persist()

    val linesAndEntryCount = busPositions.groupByKey(_.firstLine)
      .count().orderBy("count(1)").persist()

    linesAndBrigadesCount.show(300)
    linesAndEntryCount.show(300)

    //    linesAndBrigadesCount.join(linesAndBrigadesCount).show(300)

    //    linesAndBrigadesSummary.show(392)

  }

}
