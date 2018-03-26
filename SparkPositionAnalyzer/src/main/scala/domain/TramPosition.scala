package domain

import java.time.LocalDateTime

import com.esri.core.geometry.Point
import org.json4s._
import org.json4s.jackson.Serialization

case class TramPosition(line: String, brigade:String, point: Point, time:LocalDateTime) extends Serializable {
  override def toString = {
    implicit val formats = Serialization.formats(NoTypeHints)
    "{\"line\": "+line+", "+
    "\"brigade\": "+brigade+", "+
    "\"x\": \""+point.getX+"\", "+
    "\"y\": \""+point.getY+"\", "+
    "\"time\": \""+time+"\"}"
  }
}

object TramPosition {

  def convertRaw(tramPositionRaw: TramPositionRaw): TramPosition = {
    val dateTime = LocalDateTime.parse(tramPositionRaw.time)
    val point = new Point(tramPositionRaw.latitude, tramPositionRaw.longitude)
    TramPosition(tramPositionRaw.line, tramPositionRaw.brigade, point, dateTime)
  }

  def byLineAndPosition(line: String, brigade: String): TramPosition => Boolean = {
    pos => pos.line == line && pos.brigade == brigade
  }

  class CompareTramsByTime extends Ordering[TramPosition] {
    override def compare(o1: TramPosition, o2: TramPosition): Int = {
      o1.time.compareTo(o2.time)
    }
  }

}
