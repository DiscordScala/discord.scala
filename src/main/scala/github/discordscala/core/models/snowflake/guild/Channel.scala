package github.discordscala.core.models.snowflake.guild

import java.time.Instant

import github.discordscala.core._
import github.discordscala.core.models.snowflake.{Message, Snowflaked, User}
import github.discordscala.core.util._
import net.liftweb.json.Extraction
import net.liftmodules.jsonextractorng.Extraction._
import net.liftweb.json.JsonAST.JValue
import spire.math.ULong

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

case class Channel(
                    id: Option[ULong] = None,
                    `type`: Option[Int] = None, // TODO make trait and serializer
                    guildId: Option[ULong] = None,
                    position: Option[Int] = None,
                    permissionOverwrites: Option[Array[JValue]] = None, // TODO make overwrites case class
                    name: Option[String] = None,
                    topic: Option[String] = None,
                    nsfw: Option[Boolean] = None,
                    lastMessageId: Option[ULong] = None,
                    bitrate: Option[Int] = None,
                    userLimit: Option[Int] = None,
                    recipients: Option[Array[User]] = None,
                    icon: Option[String] = None,
                    ownerId: Option[ULong] = None,
                    applicationId: Option[ULong] = None,
                    parentId: Option[ULong] = None,
                    lastPinTimestamp: Option[Instant] = None
                  ) extends Snowflaked {

  override type Self = Channel

  def messagesAround(aroundId: ULong, limit: Int = 50)(implicit client: Client): Future[Either[DiscordException, Messages]] = Future {
    RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id/messages?around=$aroundId&limit=$limit", Map("Authorization" -> client.token)) match {
      case Left(e) => Left(e)
      case Right(j) => Right(new Messages(j.extractNg[Seq[Message]], this))
    }
  }

  def messagesBefore(beforeId: ULong, limit: Int = 50)(implicit client: Client): Future[Either[DiscordException, Messages]] = Future {
    RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id/messages?before=$beforeId&limit=$limit", Map("Authorization" -> client.token)) match {
      case Left(e) => Left(e)
      case Right(j) => Right(new Messages(j.extractNg[Seq[Message]], this))
    }
  }

  def messagesAfter(afterId: ULong, limit: Int = 50)(implicit client: Client): Future[Either[DiscordException, Messages]] = Future {
    RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id/messages?after=$afterId&limit=$limit", Map("Authorization" -> client.token)) match {
      case Left(e) => Left(e)
      case Right(j) => Right(new Messages(j.extractNg[Seq[Message]], this))
    }
  }

  def message(mid: ULong)(implicit client: Client): Future[Either[DiscordException, Message]] = Future {
    RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id/messages/$mid", Map("Authorization" -> client.token)) match {
      case Left(e) => Left(e)
      case Right(j) => Right(j.extractNg[Message])
    }
  }

  def postMessage(m: Message)(implicit client: Client): Future[Either[DiscordException, Message]] = Future {
    id match {
      case Some(i) =>
        RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$i/messages", Map("Authorization" -> client.token), Post, Extraction.decompose(m), Duration.Inf) match {
          case Left(e) => Left(e)
          case Right(j) => Right(j.extractNg[Message])
        }
      case None => Left(CombinedOrUnknown)
    }
  }

  def deleteMessage(m: Message)(implicit client: Client): Future[Either[DiscordException, Unit]] = Future {
    m.id match {
      case Some(messageId) =>
        RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id/messages/$messageId", Map("Authorization" -> client.token), Delete) match {
          case Left(e) => Left(e)
          case Right(j) => Right(Unit)
        }
      case None => Left(CombinedOrUnknown)
    }

  }

  override def !(implicit client: Client) : Either[DiscordException, Channel] = Channel(id.get)

}

object Channel {

  def apply(id: ULong)(implicit client: Client): Either[DiscordException, Channel] = RequestUtil.awaitRestRequestFuture(client.apiURL + s"channels/$id", Map("Authorization" -> client.token)) match {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extractNg[Channel])
  }

}

class Messages(private val messages: Seq[Message], val channel: Channel)(implicit client: Client) extends Set[Message] {

  override def contains(elem: Message): Boolean = messages.map(_.id).contains(elem.id)

  override def +(elem: Message): Set[Message] = {
    val m = Await.result(channel.postMessage(elem), Duration.Inf)
    m match {
      case Left(_) => this
      case Right(message) => new Messages(this.messages :+ message, channel)
    }
  }

  override def -(elem: Message): Set[Message] = {
    channel.deleteMessage(elem)
    new Messages(this.messages.filter(_ != elem), channel)
  }

  override def iterator: Iterator[Message] = messages.iterator

  def withPrevious(limit: Int = 50): Future[Either[DiscordException, Messages]] = Future {
    val lowestId = messages.map(_.id.get).minBy(_.signed)
    val before = Await.result(channel.messagesBefore(lowestId, limit), Duration.Inf)
    before match {
      case Left(e) => Left(e)
      case Right(m) => Right(new Messages(messages ++ m.messages, channel))
    }
  }

  def withNext(limit: Int = 50): Future[Either[DiscordException, Messages]] = Future {
    val highestId = messages.map(_.id.get).maxBy(_.signed)
    val before = Await.result(channel.messagesAfter(highestId, limit), Duration.Inf)
    before match {
      case Left(e) => Left(e)
      case Right(m) => Right(new Messages(messages ++ m.messages, channel))
    }
  }

}