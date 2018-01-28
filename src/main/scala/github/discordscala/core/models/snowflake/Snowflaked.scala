package github.discordscala.core.models.snowflake

import github.discordscala.core.util.DiscordException
import spire.math.ULong

/**
  * Snowflake
  */
trait Snowflaked {

  type Self <: Snowflaked

  val id: Option[ULong]

  def ! : Either[DiscordException, Self]

}
