package github.discordscala.core

import github.discordscala.core.event.{Sharding, WebsocketEvent}
import github.discordscala.core.models.snowflake.User
import github.discordscala.core.models.snowflake.guild.Guild
import github.discordscala.core.util._
import net.liftmodules.jsonextractorng.Extraction._
import net.liftweb.json._
import spire.math.ULong

import scala.concurrent.Future

// TODO
/**
  * Discord API Client
  * Defaults are sensible.
  *
  * @param token      Token of the Client (prefix with Bot if necessary)
  * @param gatewayURL URL of the discord-compatible Gateway (e.g. Discord or Litecord)
  * @param apiURL     URL under which the API can be found
  * @param myShards   The shards that this specific client has control over. Multiple clients may be on different servers and control different shards
  * @param sharding   Shard specification for this client
  */
case class Client(token: String, handler: PartialFunction[WebsocketEvent, Unit], gatewayURL: String = "wss://gateway.discord.gg/?v=6&encoding=json", apiURL: String = "https://discordapp.com/api/v6/", myShards: Set[Int])(implicit sharding: Sharding) {

  implicit val client: Client = this

  /**
    * List of Listeners per Shard
    */
  lazy val shards: Map[Int, Shard] = myShards.map((sc) => sc -> Shard(this, sc)).toMap

  /**
    * Access the user the client logged in as
    *
    * @return User logged in
    */
  def ourUser = User(this)

  /**
    * Get any user from the API
    * Eventually will also contain member data for all shared guilds.
    *
    * @param id ID of the user to get
    * @return User with that ID
    */
  def user(id: ULong) = Future { User(id) }

  def guild(id: ULong) = Future { Guild(id) }

  def guilds: Future[Either[DiscordException, Array[Guild]]] = Future {
    RequestUtil.awaitRestRequestFuture(apiURL + "guilds", headers = Map("Authorization" -> token), Get) match {
      case Left(e) => Left(e)
      case Right(j) => Right(j.extractNg[Array[Guild]])
    }
  }

  /**
    * Changes the username of the user logged in
    *
    * @param newUsername New username to change to
    * @return Updated User object
    */
  def username_=(newUsername: String): Future[Either[DiscordException, User]] = RequestUtil.restRequestFuture(s"${apiURL}users/@me", Map("Authorization" -> token), Patch, Extraction.decompose(User(username = Some(newUsername)))).map {
    case Left(e) => Left(e)
    case Right(j) => Right(j.extractNg[User])
  }

  /**
    * Login to the gateway
    */
  def login(): Unit = {
    shards.values.foreach(_.start())
  }

  /**
    * Logout of the gateway
    */
  def logout(): Unit = {
    shards.values.foreach(_.stop())
  }

}
