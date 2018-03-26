package domain

case class TramFrame(tramPosition1: TramPosition, tramPosition2: TramPosition, lat:Double, long:Double, distanceSpanMeters: Double, secondsSpan: Long, avgSpeedKmH: Double) {
  override def toString = {
    "{\"avgSpeedKmH\": "+avgSpeedKmH+", "+
    "\"distanceSpanMeters\": "+BigDecimal.valueOf(distanceSpanMeters).toString()+", "+
    "\"secondsSpan\": "+secondsSpan+", "+
    "\"x\": "+lat+", "+
    "\"y\": "+long+", "+
    "\"tramPosition1\": "+tramPosition1+", "+
    "\"tramPosition2\": "+tramPosition2+"}"
  }
}
