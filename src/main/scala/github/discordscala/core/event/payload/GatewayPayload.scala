package github.discordscala.core.event.payload

import github.discordscala.core.event.WebsocketEvent

/**
  * Any Gateway payload
  * All variable names are exactly the same as in the discord developer documentation.
  *
  * @param nop OP Code of the gateway payload
  */
abstract class GatewayPayload(nop: Int) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = None
  override val op: Option[Int] = Some(nop)

}
