package github.discordscala.core

import java.time.Instant

import github.discordscala.core.models.Snowflaked
import spire.math.ULong

object DiscordScala {

  val discordEpoch = ULong(1420070400000l)

  implicit class SnowflakeUtil(s: Snowflaked) {

    def toTime: Instant = {
      val depoch = s.id >> 22
      val uepoch = depoch + discordEpoch
      Instant.ofEpochMilli(uepoch.signed)
    }

  }

}
