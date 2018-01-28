package github.discordscala.core.event.opzero

import github.discordscala.core._
import github.discordscala.core.event.{WebsocketEvent, WebsocketEventBase, WebsocketListener}
import github.discordscala.core.models.snowflake.Message
import net.liftweb.json.JsonAST
import net.liftmodules.jsonextractorng.Extraction._

object MessageCreateEventBase extends WebsocketEventBase[MessageCreateEvent] {

  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): MessageCreateEvent = {
    println("creating payload")
    try {
      implicit val client: Client = c
      val m = v.extractNg[Message]
      println("creating event with payload")
      MessageCreateEvent(m)(w.shard)
    } catch {
      case e: Exception => e.printStackTrace(); null
    }
  }

  override def eventName: Option[String] = Some("MESSAGE_CREATE")

  override def eventOp: Int = 0

}

case class MessageCreateEvent(d: Message)(val shard: Shard) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = Some("MESSAGE_CREATE")
  override val op: Option[Int] = Some(0)

}
