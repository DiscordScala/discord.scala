package org.discordscala.core.models

import org.discordscala.core.models.snowflake.User
import spire.math.ULong

case class Presence(
                     user: Option[User] = None,
                     roles: Option[Array[ULong]] = None,
                     game: Option[Game] = None,
                     guildId: Option[ULong] = None,
                     status: Option[String] = None, // TODO make sealed trait and case class and custom serializer
                     since: Option[Long] = None,
                     afk: Option[Boolean] = None
                   ) {

}

case class Game(
                 name: Option[String] = None,
                 `type`: Option[Int] = None, // TODO make sealed trait and case class and custom serializer
                 url: Option[String] = None,
               )
