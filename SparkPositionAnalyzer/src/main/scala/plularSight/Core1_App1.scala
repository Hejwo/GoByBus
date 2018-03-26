package plularSight

import org.apache.spark.{SparkConf, SparkContext}

object Core1_App1 {

  def main(args: Array[String]) {
    val sc: SparkContext = getSparkContext()

    val file = sc.textFile("file:///opt/spark-2.1.0/README.md")
//    val file = sc.textFile("file:///home/codete/npm-debug.log")
    val result = file.
      flatMap(line => line.split(" ")).
      map(word => (word, 1)).
      reduceByKey(_+_).
      sortBy( pair => pair._2, false)

    result.take(10).foreach(println)
  }

  private def getSparkContext(): SparkContext = {
    val conf = new SparkConf().
      setAppName("Word Counter").setMaster("spark://192.168.1.64:7077")
    new SparkContext(conf)
  }
}
