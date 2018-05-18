package org.discordscala.core.cache

import org.discordscala.core.models.snowflake.{Message, Snowflaked, User}
import spire.math.ULong
import org.discordscala.core.cache.DiscordCache._
import org.discordscala.core.models.snowflake.guild.{Channel, Guild, GuildedMember, Member}

import scala.collection.mutable

class WeakDiscordCache extends DiscordCache {

  import WeakDiscordCache._

  override def messages: DiscordMessages = new WeakDiscordMessages

  override def channels: DiscordChannels = new WeakDiscordChannels

  override def guilds: DiscordGuilds = new WeakDiscordGuilds

  override def members: DiscordMembers = new WeakDiscordMembers

  override def users: DiscordUsers = new WeakDiscordUsers

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
