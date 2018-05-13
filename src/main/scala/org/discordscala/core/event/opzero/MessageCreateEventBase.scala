package org.discordscala.core.event.opzero

import org.discordscala.core._
import org.discordscala.core.event.{WebsocketEvent, WebsocketEventBase, WebsocketListener}
import org.discordscala.core.models.snowflake.Message
import net.liftweb.json.JsonAST

object MessageCreateEventBase extends WebsocketEventBase[MessageCreateEvent] {

  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): MessageCreateEvent = {
    try {
      implicit val client: Client = c
      val m = v.extract /*Ng*/ [Message] // TODO use NG
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
