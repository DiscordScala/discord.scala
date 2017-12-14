package github.discordscala.core.models.snowflake

import github.discordscala.core.{Client, _}
import github.discordscala.core.util.{DiscordException, RequestUtil}
import spire.math.ULong

case class User(id: Option[ULong] = None, username: Option[String] = None, discriminator: Option[String] = None, avatar: Option[String] = None, bot: Option[Boolean] = None, mfa: Option[Boolean] = None, verified: Option[Boolean] = None, email: Option[String] = None) extends Snowflaked
object User {

  def apply(c: Client, id: ULong): Either[DiscordException, User] = RequestUtil.awaitRestRequestFuture(c.apiURL + s"users/$id", Map("Authorization" -> c.token)) match {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extract[User])
  }
  def apply(c: Client): Either[DiscordException, User] = RequestUtil.awaitRestRequestFuture(c.apiURL + "users/@me", Map("Authorization" -> c.token)) match {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extract[User])
  }

}