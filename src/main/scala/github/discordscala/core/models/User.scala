package github.discordscala.core.models

import github.discordscala.core.Client
import github.discordscala.core.util.RequestUtil
import spire.math.ULong

import github.discordscala.core._

case class User(id: Option[ULong] = None, username: Option[String] = None, discriminator: Option[String] = None, avatar: Option[String] = None, bot: Option[Boolean] = None, mfa: Option[Boolean] = None, verified: Option[Boolean] = None, email: Option[String] = None) extends Snowflaked
object User {

  def apply(c: Client, id: ULong): User = RequestUtil.awaitRestRequestFuture(c.apiURL + s"users/$id", Map("Authorization" -> c.token)).extract[User]
  def apply(c: Client): User = RequestUtil.awaitRestRequestFuture(c.apiURL + "users/@me", Map("Authorization" -> c.token)).extract[User]

}