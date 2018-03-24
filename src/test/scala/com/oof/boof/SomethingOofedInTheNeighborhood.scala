package com.oof.boof

import akka.actor.{Actor, ActorSystem, Props}
import github.discordscala.core.Client
import github.discordscala.core.event.Sharding
import github.discordscala.core.event.opzero.MessageCreateEvent
import github.discordscala.core.models.snowflake.Message
import github.discordscala.core.models.snowflake.guild.Channel
import spire.math.ULong

object SomethingOofedInTheNeighborhood {

  def main(args: Array[String]): Unit = {
    implicit val sharding: Sharding = Sharding(1)
    val myActorSystem = ActorSystem("ClientActorSystem")
    val handlers = myActorSystem.actorOf(Props[Handlers], "handlers")
    val c = Client("Bot [token]", eventHandlers = Seq(handlers), myShards = Set(0))
    c.login()
    Thread.sleep(20000)
    implicit val client: Client = c
    Channel(ULong(390752467878412288l)) match {
      case Left(e) => println(e)
      case Right(ch) => ch.postMessage(Message(content = Some("dab")))
    }
  }

  class Handlers extends Actor {
    override def receive: Receive = {
      case me: MessageCreateEvent =>
        val m = me.d
        implicit val client = me.shard.client
        m.author.flatMap(_.bot) match {
          case Some(true) =>
          case _ =>
            m.content match {
              case Some(content) =>
                if(content == "chan!dab") {
                  m.channelId match {
                    case Some(cid) =>
                      Channel(cid) match {
                        case Left(e) => println(e)
                        case Right(c) =>
                          c.postMessage(Message(content = Some("dab")))
                      }
                    case None =>
                  }
                }
              case None =>
            }
        }
    }
  }

}
