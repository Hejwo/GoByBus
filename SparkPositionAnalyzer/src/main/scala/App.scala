import java.time.LocalDateTime

import domain.{BusFrame, BusPosition}
import org.apache.spark.mllib.rdd.RDDFunctions._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object App {

  def main(args: Array[String]) {
    //    val sc: SparkContext = context
    //    var rdd: RDD[String] = sc.textFile("line.csv")
  }

  def context: SparkContext = {
    val sparkConfiguration = new SparkConf().setAppName("test1").setMaster("spark://localhost:7077")
    new SparkContext(sparkConfiguration)
  }

  def mapToBusPositions(rdd: RDD[String]): RDD[BusPosition] = {
    rdd.map(BusPosition(_)).filter(_.isDefined).map(_.get)
  }

  def findCommonDay(rdd: RDD[String]): LocalDateTime = {
    mapToBusPositions(rdd).map(_.time.toLocalDateTime.toLocalDate.atStartOfDay()).map(date => (date, 1))
      .reduceByKey(_ + _).sortBy(_._2, ascending = false).map(_._1).first()
  }

  def findLongestBrigade(rdd: RDD[String]): String = {
    mapToBusPositions(rdd).map(_.brigade).map(brigade => (brigade, 1)).reduceByKey(_ + _)
      .sortBy(_._2, ascending = false).map(_._1).first()
  }

  def findLongestBrigadeDayDataSorted(rdd: RDD[String]): RDD[BusPosition] = {
    val commonDay = findCommonDay(rdd)
    val longestBrigade = findLongestBrigade(rdd)

    mapToBusPositions(rdd).
      map(busPosition => (busPosition.brigade, busPosition)).
      filter(_._1.equalsIgnoreCase(longestBrigade)).
      sortBy(_._2.time.getTime).
      map(_._2).
      filter(position => position.time.toLocalDateTime.toLocalDate.atStartOfDay.isEqual(commonDay))
  }

  def findBusFrames(rdd: RDD[BusPosition]): RDD[BusFrame] = {
    rdd.sortBy(_.time.getTime).sliding(2).collect {
      case Array(BusPosition(s1, l1, p1, t1, b1, lf1), BusPosition(s2, l2, p2, t2, b2, lf2))
        if l1 == l2 && b1 == b2 => BusFrame(s1, l1, t1, t2, p1, p2, b1, lf1)
    }
  }

}
