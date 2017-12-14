package github.discordscala.core.event.payload

import github.discordscala.core.event.WebsocketEvent

abstract class GatewayPayload(nop: Int) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = None
  override val op: Option[Int] = Some(nop)

}
