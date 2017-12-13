package github.discordscala.core.event

import java.util.ServiceLoader

import github.discordscala.core.Client

import scala.collection.JavaConverters._

class WebsocketListener(c: Client) {

  lazy val lmap: Map[String, WebsocketEventBase[_]] = ServiceLoader.load(classOf[WebsocketEventBase[_]]).asScala.map((e) => (e.eventName, e)).toMap

}
