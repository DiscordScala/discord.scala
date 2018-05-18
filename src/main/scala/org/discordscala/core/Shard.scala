package org.discordscala.core

import scala.language.implicitConversions
import akka.http.scaladsl.model.ws.TextMessage
import org.discordscala.core.event.{Sharding, WebsocketListener}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json._
import org.discordscala.core.cache.DiscordCache

case class Shard(client: Client, shardNumber: Int, cache: Option[DiscordCache])(implicit sharding: Sharding) {

  val websocketListener: WebsocketListener = new WebsocketListener(client, this, cache)

  def start(): Unit = websocketListener.start()

  def stop(): Unit = websocketListener.stop()

  def send(m: String): Unit = {
    websocketListener.currentRequest._1 ! TextMessage(m)
  }

  implicit def jValueToString(j: JValue): String = compactRender(j)

  implicit def anyRefToString(a: AnyRef): String = compactRender(Extraction.decompose(a))

}