package com.oof.noof

import github.discordscala.core.Client
import github.discordscala.core.event.Sharding

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SomethingTest {

  def main(args: Array[String]): Unit = {
    implicit val sharding: Sharding = Sharding(1)
    val c = Client("Bot MzMwOTExNTkxOTIxMDkwNTYw.DRIhqA.lQVlTGjgxe4WqycWFwpUvGqmvN4", myShards = Set(0))
    c.login()
    println(Await.result(c.username_=("dabonthehaters"), Duration.Inf))
  }

}
