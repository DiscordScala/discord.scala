package github.discordscala.core.util

import github.discordscala.core.event.{WebsocketEvent, WebsocketEventBase}

trait EventListener[E <: WebsocketEvent] {

  type Event = E

  def base: WebsocketEventBase[E]

  def apply(event: E): Unit

}
