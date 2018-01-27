package github.discordscala.core.event.opnonzero

import github.discordscala.core._
import github.discordscala.core.event.payload.GatewayPayload
import github.discordscala.core.event.{WebsocketEventBase, WebsocketListener}
import net.liftweb.json.JsonAST

object HelloEventBase extends WebsocketEventBase[HelloEvent] {

  /**
    * Creates an event from the parsed JSON payload
    *
    * @param v JSON Payload at `d`
    * @param c Client
    * @param w Websocket Listener
    * @return Websocket Event
    */
  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): HelloEvent = {
    val hd = v.extract[HelloData]
    val e = HelloEvent(hd)(w.shard)
    e
  }

  /**
    *
    * @return Name of the Event
    */
  override def eventName: Option[String] = None

  /**
    *
    * @return OP code of the event
    */
  override def eventOp: Int = 10

}

case class HelloEvent(d: HelloData)(shard: Shard) extends GatewayPayload(10)

case class HelloData(heartbeatInterval: Int)
