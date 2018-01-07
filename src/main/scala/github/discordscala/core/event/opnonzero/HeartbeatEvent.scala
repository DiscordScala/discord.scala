package github.discordscala.core.event.opnonzero

import github.discordscala.core.event.payload.GatewayPayload

case class HeartbeatEvent(d: Option[Int]) extends GatewayPayload(1)
