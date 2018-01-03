package com.oof.boof

import github.discordscala.core.Client
import github.discordscala.core.event.Sharding
import spire.math.ULong

object SomethingOofedInTheNeighborhood {

  def main(args: Array[String]): Unit = {
    implicit val sharding: Sharding = Sharding(1)
    val c = Client("Bot MzMwOTExNTkxOTIxMDkwNTYw.DSyWcw.xAeV2BbH7Np80RhZkyn2NbeTng8", myShards = Set(0))
    c.login()
    println(c.guild(ULong(390751088829005826l)))
    println(c.guild(ULong(390751088829005826l)))
    c.logout()
  }

}
