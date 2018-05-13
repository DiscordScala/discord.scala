package org.discordscala.core.models.snowflake

import java.time.Instant

import org.discordscala.core._
import org.discordscala.core.models.snowflake.guild.Channel
import org.discordscala.core.util.{DiscordException, RequestMethod, RequestUtil}
import net.liftweb.json.Extraction
import net.liftweb.json.JsonAST.JValue
import spire.math.ULong

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

/**
  * Representation of a message
  *
  * @param id               ID of the message
  * @param channelId       ID of the channel the message was sent in
  * @param author           Author of the message
  * @param content          Raw content of the message
  * @param timestamp        Instant when the message was created
  * @param editedTimestamp Instant when the message was last edited
  * @param tts              Whether or not the message was sent with TTS enabled
  * @param mentionEveryone Whether or not the message contains a mention to everyone
  * @param mentions         Users that the message mentions
  * @param mentionRoles    Roles that the message mentions
  * @param attachments      Links to the attachements of the message
  * @param embeds           JSON representation of an messages embed
  * @param reactions        Reactions added to a message
  * @param nonce            ??
  * @param pinned           Whether or not the message was pinned
  * @param webhookId       ID of the webhhok the message was sent by (if applies)
  * @param `type`           type of the message
  */
case class Message( // TODO convert JValues into their respective objects
                    id: Option[ULong] = None,
                    channelId: Option[ULong] = None,
                    author: Option[User] = None,
                    content: Option[String] = None,
                    timestamp: Option[Instant] = None,
                    editedTimestamp: Option[Instant] = None,
                    tts: Option[Boolean] = None,
                    mentionEveryone: Option[Boolean] = None,
                    mentions: Option[Array[User]] = None,
                    mentionRoles: Option[Array[ULong]] = None,
                    attachments: Option[Array[JValue]] = None,
                    embeds: Option[Array[JValue]] = None,
                    reactions: Option[Array[JValue]] = None,
                    nonce: Option[ULong] = None,
                    pinned: Option[Boolean] = None,
                    webhookId: Option[Boolean] = None,
                    `type`: Option[Int] = None
                  ) extends Snowflaked {

  override type Self = Message

  override def !(implicit client: Client): Either[DiscordException, Message] = {
    channelId.map(Channel(_)) match {
      case Some(Right(c)) => id match {
        case Some(m) => Await.result(c.message(m), Duration.Inf) match {
          case Left(n) => Left(n)
          case Right(o) => Right(o)
        }
        case None => Left(DiscordException.CombinedOrUnknown)
      }
      case Some(Left(e)) => Left(e)
      case None => Left(DiscordException.CombinedOrUnknown)
    }
  }

  def edit(newMessage: Message)(implicit client: Client): Future[Either[DiscordException, Message]] = Future {
    channelId match {
      case Some(cid) => id match {
        case Some(mid) =>
          RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$cid/messages/$mid", Map("Authorization" -> client.token), RequestMethod.Patch, body = Extraction.decompose(newMessage), timeout = Duration.Inf).map(_.extract /*Ng*/ [Message])
        case None => Left(DiscordException.CombinedOrUnknown)
      }
      case None => Left(DiscordException.CombinedOrUnknown)
    }
  }

}
