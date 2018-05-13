package org.discordscala.core.util

import org.discordscala.core.event.{WebsocketEvent, WebsocketEventBase}

trait EventListener[E <: WebsocketEvent] {

  type Event = E

  def base: WebsocketEventBase[E]

  def apply(event: E): Unit

}
