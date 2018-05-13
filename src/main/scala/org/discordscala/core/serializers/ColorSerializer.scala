package org.discordscala.core.serializers

import java.awt.Color

import net.liftweb.json.JsonAST.JInt
import net.liftweb.json._

object ColorSerializer extends Serializer[Color] {

  val colorClazz: Class[Color] = classOf[Color]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Color] = {
    case (TypeInfo(`colorClazz`, _), json) => json match {
      case JInt(h) => new Color(h.toInt)
      case x => throw new MappingException("Can't convert " + x + " to Color")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Color => JInt(x.getRGB)
  }

}
