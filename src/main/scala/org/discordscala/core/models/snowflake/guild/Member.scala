package org.discordscala.core.models.snowflake.guild

import java.time.Instant

import org.discordscala.core._
import org.discordscala.core.models.snowflake.{Snowflaked, User}
import org.discordscala.core.util.DiscordException
import org.discordscala.core.util.DiscordException.CombinedOrUnknown
import spire.math.ULong

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class Member(
                   user: Option[User] = None,
                   nick: Option[String] = None,
                   private val roles: Option[Array[String]] = None,
                   joinedAt: Option[Instant] = None,
                   deaf: Option[Boolean] = None,
                   mute: Option[Boolean] = None
                 ) {

  def memberRoles(implicit client: Client): Future[Either[DiscordException, Array[Role]]] = Future {
    val ta = roles.map(_.map(s => Await.result(Role(ULong(s.toLong)), Duration.Inf)))
    ta match {
      case Some(a) =>
        if (a.exists {
          case Left(_) => true
          case _ => false
        }) {
          Left(DiscordException.CombinedOrUnknown)
        } else {
          Right(a.filter(_.isRight).map(_.right.get).filter(_.isDefined).map(_.get))
        }
      case None => Left(DiscordException.CombinedOrUnknown)
    }
  }

}

case class GuildedMember(guild: Guild, member: Member) extends Snowflaked {

  override type Self = GuildedMember
  override val id: Option[ULong] = member.user.flatMap(_.id)

  override def !(implicit client: Client): Either[DiscordException, GuildedMember] = {
    guild.! match {
      case Left(e) => Left(e)
      case Right(g) => member.user.map(_.!) match {
        case Some(Left(e)) => Left(e)
        case None => Left(CombinedOrUnknown)
        case Some(Right(u)) => Right(u.asMemberOf(g))
      }
    }
  }

}
