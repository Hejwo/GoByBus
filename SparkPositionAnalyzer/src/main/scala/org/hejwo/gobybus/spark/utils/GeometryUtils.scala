package org.hejwo.gobybus.spark.utils

import com.esri.core.geometry.{GeometryEngine, Point, SpatialReference}

object GeometryUtils {

  private val spacialReference: SpatialReference = SpatialReference.create(4326)

  def calculateDistance(point1: (Double, Double), point2: (Double, Double)): Double = {
    val p1 = pairToPoint(point1)
    val p2 = pairToPoint(point2)

    GeometryEngine.distance(p1, p2, spacialReference)
  }

  def contains(point1: (Double, Double), point2: (Double, Double)): Boolean = {
    val p1 = pairToPoint(point1)
    val p2 = pairToPoint(point2)

    GeometryEngine.contains(p1, p2, spacialReference)
  }

  private def pairToPoint(point: (Double, Double)): Point = new Point(point._2, point._1)

}
