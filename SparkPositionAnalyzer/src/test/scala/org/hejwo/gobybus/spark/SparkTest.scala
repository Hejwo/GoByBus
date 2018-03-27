package org.hejwo.gobybus.spark

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

trait SparkTest extends FlatSpec with BeforeAndAfterAll {

  implicit val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("MainTest").getOrCreate

  override protected def afterAll() {
    spark.stop()
  }

}
