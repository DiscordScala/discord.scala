package github.discordscala.core.event.opnonzero

import github.discordscala.core._
import github.discordscala.core.Client
import github.discordscala.core.event.{WebsocketEventBase, WebsocketListener}
import github.discordscala.core.event.payload.GatewayPayload
import net.liftweb.json.JsonAST
import net.liftmodules.jsonextractorng.Extraction._

object HeartbeatEventBase extends WebsocketEventBase[HeartbeatEvent] {

  /**
    * Creates an event from the parsed JSON payload
    *
    * @param v JSON Payload at `d`
    * @param c Client
    * @param w Websocket Listener
    * @return Websocket Event
    */
  override def apply(v: JsonAST.JValue, c: Client, w: WebsocketListener): HeartbeatEvent = {
    val hd = v.extractNg[Option[Int]]
    val e = HeartbeatEvent(hd)
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
  override def eventOp: Int = 11

}

case class HeartbeatEvent(d: Option[Int]) extends GatewayPayload(11)
