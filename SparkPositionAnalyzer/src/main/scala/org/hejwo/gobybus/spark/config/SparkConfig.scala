package org.hejwo.gobybus.spark.config

import com.typesafe.config.{Config, ConfigFactory}

object SparkConfig {
  private val config = ConfigFactory.load()
  val sparkLogLevel = getConfig(config, "spark.logLevel")
  val sparkMaster = getConfig(config, "spark.master")
  val sparkAppName = getConfig(config, "spark.appName")
  val mongoUrl = getConfig(config, "spark.mongo.url")
  val mongoLocationsDatabase = getConfig(config, "spark.mongo.locationsDatabase")
  val mongoLocationsCollection = getConfig(config, "spark.mongo.locationsCollection")

  private def getConfig(config: Config, name: String): String = {
    config.getString(name)
  }

}
