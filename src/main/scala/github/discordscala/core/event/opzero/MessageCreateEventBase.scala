package github.discordscala.core.event.opzero

import github.discordscala.core.event.WebsocketEventBase

object MessageCreateEventBase extends WebsocketEventBase[MessageCreateEvent] {

}

case class MessageCreateEvent()
