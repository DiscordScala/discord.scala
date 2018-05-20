package org.discordscala.core.models.snowflake.guild

import java.awt.Color

import org.discordscala.core._
import org.discordscala.core.models.snowflake.Snowflaked
import org.discordscala.core.util.DiscordException
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
               ) extends Snowflaked {

  override type Self = Role

  def guild(implicit client: Client): Future[Either[DiscordException, Option[Guild]]] = Future {
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

  override def !(implicit client: Client): Either[DiscordException, Role] = {
    id match {
      case Some(i) => Await.result(Role(i), Duration.Inf) match {
        case Left(e) => Left(e)
        case Right(Some(r)) => Right(r)
        case Right(None) => Left(DiscordException.CombinedOrUnknown)
      }
      case None => Left(DiscordException.CombinedOrUnknown)
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
