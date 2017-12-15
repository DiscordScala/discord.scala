package github.discordscala.core.models

import github.discordscala.core.models.snowflake.User
import spire.math.ULong

case class Presence(
                     user: Option[User] = None,
                     roles: Option[Array[ULong]] = None,
                     game: Option[Game] = None,
                     guild_id: Option[ULong] = None,
                     status: Option[String] = None, // TODO make sealed trait and case class and custom serializer
                   ) {

}

case class Game(
                 name: Option[String] = None,
                 `type`: Option[Int] = None, // TODO make sealed trait and case class and custom serializer
                 url: Option[String] = None,
               )
