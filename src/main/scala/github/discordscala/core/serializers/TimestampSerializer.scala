package github.discordscala.core.serializers

import java.text.SimpleDateFormat
import java.time.Instant

import net.liftweb.json.JsonAST.JString
import net.liftweb.json._

object TimestampSerializer extends Serializer[Instant] {

  val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")

  val instantClazz: Class[Instant] = classOf[Instant]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Instant] = {
    case (TypeInfo(`instantClazz`, _), json) => json match {
      case JString(s) => sdf.parse(s.replaceAll("""\+(\d\d)(?::(\d\d))""", "+$1$2")).toInstant
      case JNull => null
      case x => throw new MappingException("Can't convert " + x + " to Instant")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Instant => JString(x.toString)
  }

}
