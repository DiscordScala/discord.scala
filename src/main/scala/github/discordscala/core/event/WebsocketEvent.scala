package github.discordscala.core.event

import net.liftweb.json.JsonAST.JValue

/**
  * Should be extended by Objects. Bases are found via reflection, so no registering should be needed.
 *
  * @tparam E the type of the Event being created
  */
trait WebsocketEventBase[E <: WebsocketEvent] {

  def apply(v: JValue): E
  def eventName: Option[String]
  def eventOp: Int

}
trait WebsocketEvent {

  val d: Any
  val s: Option[Int]
  val t: Option[String]
  val op: Option[Int]

}