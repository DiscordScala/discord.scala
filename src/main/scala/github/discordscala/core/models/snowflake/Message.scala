package github.discordscala.core.models.snowflake

import java.time.Instant

import net.liftweb.json.JsonAST.JValue
import spire.math.ULong

/**
  * Representation of a message
  *
  * @param id               ID of the message
  * @param channel_id       ID of the channel the message was sent in
  * @param author           Author of the message
  * @param content          Raw content of the message
  * @param timestamp        Instant when the message was created
  * @param edited_timestamp Instant when the message was last edited
  * @param tts              Whether or not the message was sent with TTS enabled
  * @param mention_everyone Whether or not the message contains a mention to everyone
  * @param mentions         Users that the message mentions
  * @param mention_roles    Roles that the message mentions
  * @param attachments      Links to the attachements of the message
  * @param embeds           JSON representation of an messages embed
  * @param reactions        Reactions added to a message
  * @param nonce            ??
  * @param pinned           Whether or not the message was pinned
  * @param webhook_id       ID of the webhhok the message was sent by (if applies)
  * @param `type`           type of the message
  */
case class Message( // TODO convert JValues into their respective objects
                    id: Option[ULong] = None,
                    channel_id: Option[ULong] = None,
                    author: Option[User] = None,
                    content: Option[String] = None,
                    timestamp: Option[Instant] = None,
                    edited_timestamp: Option[Instant] = None,
                    tts: Option[Boolean] = None,
                    mention_everyone: Option[Boolean] = None,
                    mentions: Option[Array[User]] = None,
                    mention_roles: Option[Array[ULong]] = None,
                    attachments: Option[Array[JValue]] = None,
                    embeds: Option[Array[JValue]] = None,
                    reactions: Option[Array[JValue]] = None,
                    nonce: Option[ULong] = None,
                    pinned: Option[Boolean] = None,
                    webhook_id: Option[Boolean] = None,
                    `type`: Option[Int] = None
                  ) extends Snowflaked {

}
