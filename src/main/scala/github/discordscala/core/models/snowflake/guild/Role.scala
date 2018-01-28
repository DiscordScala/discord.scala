package github.discordscala.core.models.snowflake.guild

import java.awt.Color

import github.discordscala.core._
import github.discordscala.core.models.snowflake.Snowflaked
import github.discordscala.core.util.{CombinedOrUnknown, DiscordException}
import spire.math.ULong

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class Role(
                 id: Option[ULong],
                 name: Option[String],
                 color: Option[Color],
                 hoist: Option[Boolean],
                 positon: Option[Int],
                 permissions: Option[Int], // TODO Convert to Permissions objects
                 managed: Option[Boolean],
                 mentionable: Option[Boolean]
               )(implicit client: Client) extends Snowflaked {

  override type Self = Role

  def guild: Future[Either[DiscordException, Option[Guild]]] = Future {
    id match {
      case Some(i) =>
        val de = Await.result(client.guilds, Duration.Inf)
        de match {
          case Left(e) => Left(e)
          case Right(ga) => Right(ga.find(_.roles match {
            case Some(ra) => ra.exists(_.id == id)
            case None => false
          }))
        }
      case None => throw new IllegalArgumentException("Role doesn't have a registered ID?")
    }
  }

  override def ! : Either[DiscordException, Role] = {
    id match {
      case Some(i) => Await.result(Role(i), Duration.Inf) match {
        case Left(e) => Left(e)
        case Right(Some(r)) => Right(r)
        case Right(None) => Left(CombinedOrUnknown)
      }
      case None => Left(CombinedOrUnknown)
    }
  }

}

object Role {

  def apply(id: ULong)(implicit client: Client): Future[Either[DiscordException, Option[Role]]] = Future {
    Await.result(client.guilds, Duration.Inf) match {
      case Left(e) => Left(e)
      case Right(j) =>
        val og = j.find(_.roles.exists(_.filter(_.id.isDefined).exists(_.id.get == id)))
        val ro = og.flatMap(_.roles.flatMap(_.filter(_.id.isDefined).find(_.id.get == id)))
        Right(ro)
    }
  }

}
