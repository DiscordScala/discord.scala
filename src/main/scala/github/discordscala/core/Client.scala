package github.discordscala.core

import github.discordscala.core.models.User
import spire.math.ULong

// TODO
case class Client(token: String, gatewayURL: String = "wss://gateway.discord.gg/", apiURL: String = "https://discordapp.com/api/v6/") {

  def ourUser = User(this)
  def user(id: ULong) = User(this, id)

}
