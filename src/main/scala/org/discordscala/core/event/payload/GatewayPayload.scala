package org.discordscala.core.event.payload

import org.discordscala.core.event.WebsocketEvent

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
