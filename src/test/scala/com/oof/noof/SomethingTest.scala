package com.oof.noof

import github.discordscala.core.Client

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SomethingTest extends App {

  val c = Client("Bot MzMwOTExNTkxOTIxMDkwNTYw.DRIhqA.lQVlTGjgxe4WqycWFwpUvGqmvN4")
  println(Await.result(c.username_=("dabonthehaters"), Duration.Inf))

}
