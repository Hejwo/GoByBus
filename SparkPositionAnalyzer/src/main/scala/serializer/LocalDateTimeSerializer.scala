package serializer

import java.time.LocalDateTime

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JField, JString}

class LocalDateTimeSerializer extends CustomSerializer[LocalDateTime](format => (
  { case JField("time", JString(name)) => LocalDateTime.parse(name) },
  { case time:LocalDateTime => JString(time.toString)}
))
