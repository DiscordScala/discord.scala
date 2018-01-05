package github.discordscala.core.serializers

import github.discordscala.core.models.Region
import net.liftweb.json.JsonAST.JString
import net.liftweb.json._

object RegionSerializer extends Serializer[Region] {

  val regionClazz: Class[Region] = classOf[Region]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Region] = {
    case (TypeInfo(`regionClazz`, _), json) => json match {
      case JString(s) => Region(s)
      case x => throw new MappingException(s"Can't convert $x to Region")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Region => JString(x.id)
  }

}