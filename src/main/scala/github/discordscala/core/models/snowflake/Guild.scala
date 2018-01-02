package github.discordscala.core.models.snowflake

import java.time.Instant

import github.discordscala.core._
import github.discordscala.core.models.Presence
import github.discordscala.core.util.{DiscordException, RequestUtil}
import net.liftweb.json.FieldSerializer
import net.liftweb.json.FieldSerializer._
import net.liftweb.json.JsonAST.JValue
import spire.math.ULong

class Guild(
             id: Option[ULong] = None,
             name: Option[String] = None,
             icon: Option[String] = None,
             splash: Option[String] = None,
             owner: Option[Boolean] = None,
             ownerId: Option[ULong] = None,
             permissions: Option[Int] = None, // TODO Make a sealed permissions trait and case objects with a custom serializer
             region: Option[String] = None, // TODO Make a sealed region trait and case objects with a custom serializer
             afkChannelId: Option[ULong] = None,
             afkTimeout: Option[Int] = None,
             embedEnabled: Option[Boolean] = None,
             embed_channel_id: Option[ULong] = None,
             verification_level: Option[Int] = None, // TODO Make a sealed verification level trait and case objects with a custom serializer
             default_message_notifications: Option[Int] = None, // TODO ^ ditto
             explicit_content_filter: Option[Int] = None, // TODO Sealed trait, case object, blah blah
             roles: Option[Array[JValue]] = None, // TODO implement role
             emojis: Option[Array[JValue]] = None, // TODO implement emoji
             features: Option[Array[String]] = None, // TODO help im stuck in a "make a comment about case objects" factory
             mfa_level: Option[Int] = None, // TODO same here im also forced to do this "case object sealed trait" thing
             appliction_id: Option[ULong] = None,
             widget_enabled: Option[Boolean] = None,
             widget_channel_id: Option[ULong] = None,
             system_channel_id: Option[ULong] = None,
             joined_at: Option[Instant] = None,
             large: Option[Boolean] = None,
             unavailable: Option[Boolean] = None,
             member_count: Option[Int] = None,
             voice_states: Option[Array[JValue]] = None, // TODO implement partial voice states
             members: Option[Array[User]] = None, // TODO make User associated with guild by having partial Member values
             channels: Option[Array[JValue]] = None, // TODO implement channel
             presences: Option[Array[Presence]] = None,
           ) {

}

object Guild {

  val fieldSerializers: List[FieldSerializer[Guild]] = FieldSerializer[Guild](
    renameTo("ownerId", "owner_id"),
    renameFrom("owner_id", "ownerId")
  ) :: FieldSerializer[Guild](
    renameTo("afkChannelId", "afk_channel_id"),
    renameFrom("afk_channel_id", "afkChannelId")
  ) :: FieldSerializer[Guild](
    renameTo("afkTimeout", "afk_timeout"),
    renameFrom("afk_timeout", "afkTimeout")
  ) :: FieldSerializer[Guild](
    renameTo("embedEnabled", "embed_enabled"),
    renameFrom("embed_enabled", "embedEnabled")
  ) :: Nil

  def apply(c: Client, id: ULong): Either[DiscordException, Guild] = RequestUtil.awaitRestRequestFuture(c.apiURL + s"guilds/$id", Map("Authorization" -> c.token)) match {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extract[Guild])
  }

}
