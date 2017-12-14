package github.discordscala.core.event.opzero

import github.discordscala.core.event.{WebsocketEvent, WebsocketEventBase}
import net.liftweb.json.JsonAST

object MessageCreateEventBase extends WebsocketEventBase[MessageCreateEvent] {

  override def apply(v: JsonAST.JValue): MessageCreateEvent = ???

  override def eventName: Option[String] = Some("MESSAGE_CREATE")

  override def eventOp: Int = 0

}

case class MessageCreateEvent() extends WebsocketEvent {

  override val d: Any = null
  override val s: Option[Int] = None
  override val t: Option[String] = Some("MESSAGE_CREATE")
  override val op: Option[Int] = Some(0)

}
