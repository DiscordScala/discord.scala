package github.discordscala.core.serializers

import github.discordscala.core.event.WebsocketEvent
import net.liftweb.json._

object WebsocketEventSerializer extends Serializer[WebsocketEvent] {

  val eventClazz = classOf[WebsocketEvent]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), WebsocketEvent] = {
    case (TypeInfo(`eventClazz`, _), json) => json match {
      case JObject(JField("d", JObject(ed)) :: JField("t", JString(et)) :: JField("s", JInt(es)) :: JField("op", JInt(eop)) :: Nil) => {
        new WebsocketEvent {
          override type Event = this.type
          override val op: Option[Int] = Option(eop).map(_.intValue())
          override val d: Any = Option(ed)
          override val s: Option[Int] = Option(es).map(_.intValue())
          override val t: Option[String] = Option(et)
        }
      }
      case x => throw new MappingException("Can't convert " + x + " to WebsocketEvent")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case e: WebsocketEvent =>
      JObject(JField("d", Option(e.d) match {
        case Some(Some(oi)) => Extraction.decompose(oi)
        case Some(None) => JNull
        case Some(a) => Extraction.decompose(a)
        case None => JNull
      }) :: JField("t", Extraction.decompose(e.t)) :: JField("s", Extraction.decompose(e.s)) :: JField("op", Extraction.decompose(e.op)) :: Nil)
  }

}
