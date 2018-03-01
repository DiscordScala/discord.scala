package com.oof.boof

import akka.actor.ActorSystem
import github.discordscala.core.Client
import github.discordscala.core.event.Sharding
import github.discordscala.core.models.{Permission, Permissions}
import github.discordscala.core.models.snowflake.Message
import github.discordscala.core.models.snowflake.guild.Channel
import spire.math.ULong

object SomethingOofedInTheNeighborhood {

  def main(args: Array[String]): Unit = {
    /*implicit val sharding: Sharding = Sharding(1)
    val myActorSystem = ActorSystem("ClientActorSystem")
    val c = Client("Bot [token]", myShards = Set(0))
    c.login()
    Thread.sleep(20000)
    implicit val client: Client = c
    Channel(ULong(390752467878412288l)) match {
      case Left(e) => println(e)
      case Right(ch) => ch.postMessage(Message(content = Some("dab")))
    }*/
    println(Permissions(Permission.ReadMessageHistory))

    println(0x8349 & 0x0002)

    0x8349l match {
      case Permissions(x) => println(x)
      case _ =>
    }
  }

}
