package github.discordscala.core

import github.discordscala.core.event.{Sharding, WebsocketListener}

case class Shard(client: Client, shardNumber: Int)(implicit sharding: Sharding) {

  val websocketListener: WebsocketListener = new WebsocketListener(client, this)

  def start(): Unit = websocketListener.start()
  def stop(): Unit = websocketListener.stop()

}