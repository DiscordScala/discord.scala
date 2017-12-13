package github.discordscala.core.models

import github.discordscala.core.Client
import github.discordscala.core.util.RequestUtil
import spire.math.ULong

import github.discordscala.core.DiscordScala._

case class User(id: ULong, username: String, discriminator: String, avatar: String, bot: Option[Boolean], mfa: Option[Boolean], verified: Option[Boolean], email: Option[String]) extends Snowflaked
object User {

  def apply(c: Client, id: ULong): User = RequestUtil.awaitRestRequestFuture(c.apiURL + s"users/$id", Map("Authorization" -> c.token)).extract[User]
  def apply(c: Client): User = RequestUtil.awaitRestRequestFuture(c.apiURL + "users/@me", Map("Authorization" -> c.token)).extract[User]

}