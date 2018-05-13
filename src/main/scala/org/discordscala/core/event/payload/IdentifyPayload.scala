package org.discordscala.core.event.payload

import org.discordscala.core.models.Presence
import org.discordscala.core._
import org.discordscala.core.event.WebsocketEvent

/**
  * IDENTIFY Payload of the Websocket (OP 2)
  *
  * @param d Data sent by the Gateway
  */
case class IdentifyPayload(d: GatewayIdentificationData) extends WebsocketEvent {

  override val s: Option[Int] = None
  override val t: Option[String] = None
  override val op: Option[Int] = Some(2)

}

/**
  * Data sent by the Gateway. See Discord Gateway documentation.
  *
  * @param token           Token of identifying client
  * @param properties      Properties for identifying the system
  * @param compress        Whether to compress events via zlib
  * @param large_threshold The number of users to show until no more offline users are shown
  * @param shard           The current shard ID
  * @param presence        Presence data
  */
case class GatewayIdentificationData(token: String, properties: GatewayIdentificationProperties = GatewayIdentificationProperties(), compress: Boolean = false, large_threshold: Int = 100, shard: Array[Int] = Array(0, 1), presence: Option[Presence] = Some(Presence(status = Some("online"))))

/**
  * Connection Properties sent by the Gateway. See Discord Gateway Documentation.
  * Defaults are sensible.
  *
  * @param $os      Operating System of the Client
  * @param $browser Browser of the Client
  * @param device   Identifier of the Client
  */
case class GatewayIdentificationProperties($os: String = System.getProperty("os.name"), $browser: String = userAgentName, device: String = userAgentName)