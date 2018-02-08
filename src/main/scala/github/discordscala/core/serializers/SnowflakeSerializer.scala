package github.discordscala.core.serializers

import net.liftweb.json.JsonAST.JString
import net.liftweb.json._
import spire.math.ULong

object SnowflakeSerializer extends Serializer[Long] {

  val longClazz: Class[Long] = classOf[Long]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Long] = {
    case (TypeInfo(`longClazz`, _), json) => json match {
      case JString(s) => s.toLong
      case JInt(i) => i.toLong
      case x => throw new MappingException("Can't convert " + x + " to Long")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Long => JString(x.toString)
  }

}

object USnowflakeSerializer extends Serializer[ULong] {

  val uLongClazz: Class[ULong] = classOf[ULong]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), ULong] = {
    case (TypeInfo(`uLongClazz`, _), json) => json match {
      case JString(s) => ULong(s)
      case JInt(i) => ULong(i.toLong)
      case x => throw new MappingException("Can't convert " + x + " to ULong")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: ULong => JString(x.toString())
  }

}
