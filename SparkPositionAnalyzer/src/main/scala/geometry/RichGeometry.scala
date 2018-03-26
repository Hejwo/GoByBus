package geometry

import com.esri.core.geometry.{Geometry, GeometryEngine, SpatialReference}

case class RichGeometry(geometry: Geometry,
                        spacialReference: SpatialReference = SpatialReference.create(4326)) {
  def area2D() = geometry.calculateArea2D()
  def contains(other: Geometry): Boolean = GeometryEngine.contains(geometry, other, spacialReference)
  def distance(other: Geometry): Double = GeometryEngine.distance(geometry, other, spacialReference)
}

object RichGeometry {
  implicit def wrapGeometry(geometry: Geometry): RichGeometry = RichGeometry(geometry)
}