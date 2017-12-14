package github.discordscala.core.event.payload

import github.discordscala.core.models.Presence

/**
  * IDENTIFY Payload of the Websocket (OP 2)
  *
  * @param id Data sent by the Gateway
  */
case class IdentifyPayload(id: GatewayIdentificationData) extends GatewayPayload(2) {
  override val d: Any = id
}

/**
  * Data sent by the Gateway. See Discord Gateway documentation.
  *
  * @param token
  * @param properties
  * @param compress
  * @param large_threshold
  * @param shard
  * @param presence
  */
case class GatewayIdentificationData(token: String, properties: GatewayIdentificationProperties = GatewayIdentificationProperties(), compress: Boolean = false, large_threshold: Int = 100, shard: Array[Int] = Array(0, 1), presence: Option[Presence] = None)

/**
  * Connection Properties sent by the Gateway. See Discord Gateway Documentation.
  * Defaults are sensible.
  *
  * @param $os      Operating System of the Client
  * @param $browser Browser of the Client
  * @param device   Identifier of the Client
  */
case class GatewayIdentificationProperties($os: String = System.getProperty("os.name"), $browser: String = "discord.scala", device: String = "discord.scala")