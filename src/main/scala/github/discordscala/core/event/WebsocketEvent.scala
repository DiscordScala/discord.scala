package github.discordscala.core.event

import net.liftweb.json.JsonAST.JValue

/**
  * Should be extended by Objects
 *
  * @tparam E the type of the Event being created
  */
trait WebsocketEventBase[E <: WebsocketEvent] {

  def apply(v: JValue): E
  def eventName: String

}
trait WebsocketEvent