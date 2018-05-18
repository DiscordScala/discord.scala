package org.discordscala.core.models.snowflake

import org.discordscala.core.Client
import org.discordscala.core.util.DiscordException
import spire.math.ULong

/**
  * Snowflake
  */
trait Snowflaked extends Ordered[Snowflaked] {

  type Self <: Snowflaked

  val id: Option[ULong]

  def !(implicit client: Client): Either[DiscordException, Self]

  final override def compare(that: Snowflaked): Int = (that.id.getOrElse(ULong(-1)) - this.id.getOrElse(ULong(-1))).toInt

}
