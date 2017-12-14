package github.discordscala.core.event

import net.liftweb.json.JsonAST.JValue

/**
  * Should be extended by Objects. Bases are found via reflection, so no registering should be needed.
  *
  * @tparam E the type of the Event being created
  */
trait WebsocketEventBase[E <: WebsocketEvent] {

  /**
    * Creates an event from the parsed JSON payload
    *
    * @param v JSON Payload
    * @return Websocket Event
    */
  def apply(v: JValue): E

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

  val d: Any
  val s: Option[Int]
  val t: Option[String]
  val op: Option[Int]

}