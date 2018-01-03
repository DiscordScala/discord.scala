package com.oof.boof

import github.discordscala.core.Client
import github.discordscala.core.event.Sharding

object SomethingOofedInTheNeighborhood {

  def main(args: Array[String]): Unit = {
    implicit val sharding: Sharding = Sharding(1)
    val c = Client("Bot [token]", myShards = Set(0))
    c.login()
  }

}
