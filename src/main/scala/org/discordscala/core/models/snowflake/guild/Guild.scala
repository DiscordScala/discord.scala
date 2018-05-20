package org.discordscala.core.models.snowflake.guild

import java.time.Instant

import net.liftmodules.jsonextractorng.Extraction._
import net.liftweb.json.JsonAST.JValue
import org.discordscala.core._
import org.discordscala.core.models.snowflake.{Snowflaked, User}
import org.discordscala.core.models.{Presence, Region}
import org.discordscala.core.util.{DiscordException, RequestUtil}
import spire.math.ULong

case class Guild(
                  id: Option[ULong] = None,
                  name: Option[String] = None,
                  icon: Option[String] = None,
                  splash: Option[String] = None,
                  owner: Option[Boolean] = None,
                  ownerId: Option[ULong] = None,
                  permissions: Option[Int] = None, // TODO Make a sealed permissions trait and case objects with a custom serializer
                  region: Option[Region] = None,
                  afkChannelId: Option[ULong] = None,
                  afkTimeout: Option[Int] = None,
                  embedEnabled: Option[Boolean] = None,
                  embedChannelId: Option[ULong] = None,
                  verificationLevel: Option[Int] = None, // TODO Make a sealed verification level trait and case objects with a custom serializer
                  defaultMessageNotifications: Option[Int] = None, // TODO ^ ditto
                  explicitContentFilter: Option[Int] = None, // TODO Sealed trait, case object, blah blah
                  roles: Option[Array[Role]] = None,
                  emojis: Option[Array[JValue]] = None, // TODO implement emoji
                  features: Option[Array[String]] = None, // TODO help im stuck in a "make a comment about case objects" factory
                  mfaLevel: Option[Int] = None, // TODO same here im also forced to do this "case object sealed trait" thing
                  applicationId: Option[ULong] = None,
                  widgetEnabled: Option[Boolean] = None,
                  widgetChannelId: Option[ULong] = None,
                  systemChannelId: Option[ULong] = None,
                  joinedAt: Option[Instant] = None,
                  large: Option[Boolean] = None,
                  unavailable: Option[Boolean] = None,
                  memberCount: Option[Int] = None,
                  voiceStates: Option[Array[JValue]] = None, // TODO implement partial voice states
                  members: Option[Array[User]] = None, // TODO make User associated with guild by having partial Member values
                  channels: Option[Array[JValue]] = None, // TODO implement channel
                  presences: Option[Array[Presence]] = None,
                ) extends Snowflaked {

  override type Self = Guild

  override def !(implicit client: Client): Either[DiscordException, Guild] = {
    id match {
      case Some(i) => Guild(i)
      case None => Left(DiscordException.CombinedOrUnknown)
    }
  }

}

object Guild {

  def apply(id: ULong)(implicit client: Client): Either[DiscordException, Guild] = RequestUtil.awaitRestRequestFuture(client.apiURL + s"guilds/$id", Map("Authorization" -> client.token)) match {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extractNg[Guild])
  }

}
