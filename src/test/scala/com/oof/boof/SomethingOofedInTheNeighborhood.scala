package com.oof.boof

import akka.actor.{Actor, ActorSystem, Props}
import github.discordscala.core.Client
import github.discordscala.core.event.Sharding
import github.discordscala.core.event.opzero.MessageCreateEvent

object SomethingOofedInTheNeighborhood {

  def main(args: Array[String]): Unit = {
    implicit val sharding: Sharding = Sharding(1)
    val myActorSystem = ActorSystem("ClientActorSystem")
    val c = Client("Bot [token]", myShards = Set(0), eventHandlers = Seq(
      {
        class DabListener extends Actor {
          override def receive: Receive = {
            case MessageCreateEvent(m) =>
              if(m.content.get == "dab" && !m.author.get.bot.get) {
                println("dab")
              }
          }
        }
        myActorSystem.actorOf(Props(new DabListener), "dabListener")
      }
    ))
    c.login()
  }

}
