package org.discordscala.core.cache

import org.discordscala.core.cache.DiscordCache._
import org.discordscala.core.models.snowflake.guild.{Channel, Guild, GuildedMember}
import org.discordscala.core.models.snowflake.{Message, Snowflaked, User}
import spire.math.ULong

import scala.collection.mutable

class WeakDiscordCache extends DiscordCache {

  import WeakDiscordCache._

  private val (messageCache, channelCache, guildCache, memberCache, userCache) = (
    new WeakDiscordMessages,
    new WeakDiscordChannels,
    new WeakDiscordGuilds,
    new WeakDiscordMembers,
    new WeakDiscordUsers
  )

  override def messages: DiscordMessages = messageCache

  override def channels: DiscordChannels = channelCache

  override def guilds: DiscordGuilds = guildCache

  override def members: DiscordMembers = memberCache

  override def users: DiscordUsers = userCache

}

object WeakDiscordCache {

  private[WeakDiscordCache] sealed class WeakDiscordCacheObject[T <: Snowflaked] extends DiscordCacheObject[T] {

    private[this] val map = mutable.WeakHashMap[Option[ULong], Traversable[T]]()

    override def +=(t: T): Unit = {
      val entry = map.getOrElse(t.id, Seq[T]())
      val ns = (entry.toSeq :+ t).sorted(ord = Ordering[Snowflaked])
      map += (t.id -> ns)
    }

    override def /(t: T): Traversable[T] = map.getOrElse(t.id, Seq[T]())

  }

  class WeakDiscordMessages extends WeakDiscordCacheObject[Message] with DiscordMessages

  class WeakDiscordChannels extends WeakDiscordCacheObject[Channel] with DiscordChannels

  class WeakDiscordGuilds extends WeakDiscordCacheObject[Guild] with DiscordGuilds

  class WeakDiscordMembers extends WeakDiscordCacheObject[GuildedMember] with DiscordMembers

  class WeakDiscordUsers extends WeakDiscordCacheObject[User] with DiscordUsers

}
