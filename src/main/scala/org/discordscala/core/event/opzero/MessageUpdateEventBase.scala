package org.discordscala.core.event.opzero

import net.liftweb.json.JsonAST
import org.discordscala.core._
import org.discordscala.core.event.{WebsocketEvent, WebsocketEventBase, WebsocketListener}
import org.discordscala.core.models.snowflake.Message

object MessageUpdateEventBase extends WebsocketEventBase[MessageUpdateEvent] {

  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): MessageUpdateEvent = {
    try {
      implicit val client: Client = c
      val m = v.extract /*Ng*/ [Message] // TODO use NG
      MessageUpdateEvent(m)(w.shard)
    } catch {
      case e: Exception => e.printStackTrace(); null
    }
  }

  override def eventName: Option[String] = Some("MESSAGE_UPDATE")

  override def eventOp: Int = 0

}

case class MessageUpdateEvent(d: Message)(val shard: Shard) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = Some("MESSAGE_UPDATE")
  override val op: Option[Int] = Some(0)

}
