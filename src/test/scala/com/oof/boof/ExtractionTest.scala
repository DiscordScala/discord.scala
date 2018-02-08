package com.oof.boof

import github.discordscala.core._
import github.discordscala.core.models.snowflake.guild.Channel
import net.liftweb.json._
import net.liftmodules.jsonextractorng.Extraction._

object ExtractionTest {

  def main(args: Array[String]): Unit = {
    val json = """{
                 |    "id": "41771983423143937",
                 |    "guildId": "41771983423143937",
                 |    "name": "general",
                 |    "type": 0,
                 |    "position": 6,
                 |    "permissionOverwrites": [],
                 |    "nsfw": true,
                 |    "topic": "24/7 chat about how to gank Mike #2",
                 |    "lastMessageId": "155117677105512449",
                 |    "parentId": "399942396007890945"
                 |}""".stripMargin
    val jvalue = parse(json)
    val channel = jvalue.extractNg[Channel]
  }

}
