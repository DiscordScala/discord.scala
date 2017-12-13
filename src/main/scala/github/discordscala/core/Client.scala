package github.discordscala.core

import github.discordscala.core.models.User
import github.discordscala.core.util.{Patch, RequestUtil}
import net.liftweb.json
import spire.math.ULong
import net.liftweb.json._

import scala.concurrent.Future

// TODO
case class Client(token: String, gatewayURL: String = "wss://gateway.discord.gg/", apiURL: String = "https://discordapp.com/api/v6/") {

  def ourUser = User(this)
  def user(id: ULong) = User(this, id)

  def username_=(newUsername: String): Future[json.JValue] = RequestUtil.restRequestFuture(s"${apiURL}users/@me", Map("Authorization" -> token), Patch, Extraction.decompose(User(username = Some(newUsername))))

}
