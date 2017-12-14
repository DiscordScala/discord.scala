package github.discordscala.core.models.snowflake

import spire.math.ULong

/**
  * Snowflake
  */
trait Snowflaked {

  val id: Option[ULong]

}
