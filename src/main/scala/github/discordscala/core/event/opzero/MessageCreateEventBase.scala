package github.discordscala.core.event.opzero

import github.discordscala.core._
import github.discordscala.core.event.{WebsocketEvent, WebsocketEventBase, WebsocketListener}
import github.discordscala.core.models.snowflake.Message
import net.liftweb.json.JsonAST

object MessageCreateEventBase extends WebsocketEventBase[MessageCreateEvent] {

  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): MessageCreateEvent = MessageCreateEvent(v.extract[Message], w.shard)

  override def eventName: Option[String] = Some("MESSAGE_CREATE")

  override def eventOp: Int = 0

}

case class MessageCreateEvent(d: Message, shard: Shard) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = Some("MESSAGE_CREATE")
  override val op: Option[Int] = Some(0)

}
