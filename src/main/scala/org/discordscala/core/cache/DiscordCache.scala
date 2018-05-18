package org.discordscala.core.cache

import org.discordscala.core.models.snowflake.guild.{Channel, Guild, GuildedMember}
import org.discordscala.core.models.snowflake.{Message, Snowflaked, User}

trait DiscordCache {

  import DiscordCache._

  def messages: DiscordMessages
  def channels: DiscordChannels
  def guilds: DiscordGuilds
  def members: DiscordMembers
  def users: DiscordUsers

}

object DiscordCache {

  private[cache] trait DiscordCacheObject[T <: Snowflaked] {
    def +=(t: T): Unit
    def /(t: T): Traversable[T]
  }

  trait DiscordMessages extends DiscordCacheObject[Message]
  trait DiscordChannels extends DiscordCacheObject[Channel]
  trait DiscordGuilds extends DiscordCacheObject[Guild]
  trait DiscordMembers extends DiscordCacheObject[GuildedMember]
  trait DiscordUsers extends DiscordCacheObject[User]

}
