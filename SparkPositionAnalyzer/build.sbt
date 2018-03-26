name := "SparkPositionAnalyzer"
version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies += "com.esri.geometry" % "esri-geometry-api" % "1.2.1"
libraryDependencies += "com.github.nscala-time" % "nscala-time_2.9.1" % "1.4.0"

libraryDependencies += "org.json4s" % "json4s-native_2.11" % "3.5.1"
libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.5.1"

val jacksonVersion = "2.6.7"
dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
)

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6"
libraryDependencies += "junit" % "junit" % "4.12" % "test"


libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.0.0"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.0.0"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.0.0"
libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector_2.11" % "2.2.0"
