package github.discordscala.core.event

import github.discordscala.core.Client
import net.liftweb.json.JsonAST.JValue

/**
  * Should be extended by Objects. Bases are found via reflection, so no registering should be needed.
  *
  * @tparam E the type of the Event being created
  */
trait WebsocketEventBase[E <: WebsocketEvent] {

  type Event = E

  /**
    * Creates an event from the parsed JSON payload
    *
    * @param v JSON Payload at `d`
    * @param c Client
    * @param w Websocket Listener
    * @return Websocket Event
    */
  def apply(v: JValue, c: Client, w: WebsocketListener): Event

  /**
    *
    * @return Name of the Event
    */
  def eventName: Option[String]

  /**
    *
    * @return OP code of the event
    */
  def eventOp: Int

}

/**
  * Trait modelling any websocket event. Used together with WebsocketEventBase
  * All variables are named accordingly to the Discord Gateway documentation.
  */
trait WebsocketEvent {

  type Event

  val d: Any
  val s: Option[Int]
  val t: Option[String]
  val op: Option[Int]

}