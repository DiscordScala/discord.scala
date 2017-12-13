package github.discordscala.core

import java.time.Instant

import github.discordscala.core.models.Snowflaked
import github.discordscala.core.serializers.SnowflakeSerializer
import net.liftweb.json.{DefaultFormats, Formats}
import spire.math.ULong

object DiscordScala {

  val discordEpoch = ULong(1420070400000l)
  val userAgent: (String, String) = "User-Agent" -> "discord.scala/0.1"
  implicit val formats: Formats = DefaultFormats + SnowflakeSerializer

  implicit class SnowflakeUtil(s: Snowflaked) {

    def toTime: Instant = {
      val depoch = s.id >> 22
      val uepoch = depoch + discordEpoch
      Instant.ofEpochMilli(uepoch.signed)
    }

  }

}
