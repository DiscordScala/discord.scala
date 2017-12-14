package github.discordscala.core.models.snowflake

import java.time.Instant

import net.liftweb.json.JsonAST.JValue
import spire.math.ULong

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
