package github.discordscala.core.event.payload

import github.discordscala.core.models.Presence

case class IdentifyPayload(id: GatewayIdentificationData) extends GatewayPayload(2) {
  override val d: Any = id
}

case class GatewayIdentificationData(token: String, properties: GatewayIdentificationProperties = GatewayIdentificationProperties(), compress: Boolean = false, large_threshold: Int = 100, shard: Array[Int] = Array(0, 1), presence: Option[Presence] = None)

case class GatewayIdentificationProperties($os: String = System.getProperty("os.name"), $browser: String = "discord.scala", device: String = "discord.scala")