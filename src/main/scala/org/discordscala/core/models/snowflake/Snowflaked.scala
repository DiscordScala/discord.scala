package org.discordscala.core.models.snowflake

import org.discordscala.core.Client
import org.discordscala.core.util.DiscordException
import spire.math.ULong

/**
  * Snowflake
  */
trait Snowflaked {

  type Self <: Snowflaked

  val id: Option[ULong]

  def !(implicit client: Client): Either[DiscordException, Self]

}
