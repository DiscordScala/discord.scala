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

  class WeakDiscordMessages extends WeakDiscordCacheObject[Message] with DiscordMessages {

    override def +=(t: Message, interpolate: Boolean): Unit = {
      if(interpolate) {
        val l = /(t).last
        val s = Message(
          if (t.id.isDefined) t.id else l.id,
          if (t.channelId.isDefined) t.channelId else l.channelId,
          if (t.author.isDefined) t.author else l.author,
          if (t.content.isDefined) t.content else l.content,
          if (t.timestamp.isDefined) t.timestamp else l.timestamp,
          if (t.editedTimestamp.isDefined) t.editedTimestamp else l.editedTimestamp,
          if (t.tts.isDefined) t.tts else l.tts,
          if (t.mentionEveryone.isDefined) t.mentionEveryone else l.mentionEveryone,
          if (t.mentions.isDefined) t.mentions else l.mentions,
          if (t.mentionRoles.isDefined) t.mentionRoles else l.mentionRoles,
          if (t.attachments.isDefined) t.attachments else l.attachments,
          if (t.embeds.isDefined) t.embeds else l.embeds,
          if (t.reactions.isDefined) t.reactions else l.reactions,
          if (t.nonce.isDefined) t.nonce else l.nonce,
          if (t.pinned.isDefined) t.pinned else l.pinned,
          if (t.webhookId.isDefined) t.webhookId else l.webhookId,
          if (t.`type`.isDefined) t.`type` else l.`type`
        )
        super.+=(s)
      } else {
        super.+=(t)
      }
    }

  }

  class WeakDiscordChannels extends WeakDiscordCacheObject[Channel] with DiscordChannels

  class WeakDiscordGuilds extends WeakDiscordCacheObject[Guild] with DiscordGuilds

  class WeakDiscordMembers extends WeakDiscordCacheObject[GuildedMember] with DiscordMembers

  class WeakDiscordUsers extends WeakDiscordCacheObject[User] with DiscordUsers

}
