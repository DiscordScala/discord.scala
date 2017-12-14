package github.discordscala.core.models.snowflake

import spire.math.ULong

trait Snowflaked {

  val id: Option[ULong]

}
