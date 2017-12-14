package github.discordscala.core.event

import java.util.ServiceLoader

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Client => _, _}
import github.discordscala.core._
import github.discordscala.core.event.payload.{GatewayIdentificationData, IdentifyPayload}
import net.liftweb.json._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Future
import scala.math.BigInt

class WebsocketListener(c: Client, chooseShard: Option[Int] = None)(implicit sharding: Sharding) {

  implicit val system: ActorSystem = ActorSystem("WebsocketListenerSystem")
  implicit val materializer: Materializer = ActorMaterializer()

  lazy val opZeroMap: Map[String, WebsocketEventBase[_ <: WebsocketEvent]] = ServiceLoader.load(classOf[WebsocketEventBase[_ <: WebsocketEvent]]).asScala.filter(_.eventName.isDefined).map((e) => (e.eventName.get, e)).toMap
  lazy val opNonZeroMap: Map[Int, WebsocketEventBase[_ <: WebsocketEvent]] = ServiceLoader.load(classOf[WebsocketEventBase[_ <: WebsocketEvent]]).asScala.filter(_.eventOp != 0).map((e) => (e.eventOp, e)).toMap

  chooseShard match {
    case Some(shardChose) => sharding.myListenerShards += (shardChose -> this)
    case None => sharding.addListener(this)
  }

  lazy val req = WebSocketRequest(c.gatewayURL)

  lazy val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 1000, OverflowStrategy.fail)
  lazy val messageSink: Sink[Message, NotUsed] = Flow[Message].map(handleGateway).to(Sink.ignore)

  def startRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = {
    val r = Http().webSocketClientFlow(req)
    messageSource.viaMat(r.viaMat(KillSwitches.single)(Keep.both))(Keep.both).toMat(messageSink)(Keep.left).run()
  }

  var currentRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = _

  def start(): Unit = {
    currentRequest = startRequest
    currentRequest._1.identify()
  }

  def stop(): Unit = {
    currentRequest._2._2.shutdown()
    currentRequest = null
  }

  def handleGateway(m: Message): Unit = {
    val js = m.asTextMessage.getStrictText
    val jast = parse(js)
    jast \ "op" match {
      case JInt(b) if b == BigInt(0) => jast \ "t" match {
        case JString(e) => opZeroMap(e).apply(jast \ "d")
      }
      case JInt(b) => opNonZeroMap(b.toInt).apply(jast \ "d")
    }
  }

  implicit class GatewayWebsocket(ar: ActorRef) {

    def identify(): Future[Unit] = Future {
      ar ! TextMessage(compactRender(Extraction.decompose(IdentifyPayload(GatewayIdentificationData(c.token, shard = Array(sharding.myListenerShards.find(_._2 == WebsocketListener.this).get._1, sharding.max_shards))))))
    }

  }

}

case class Sharding(max_shards: Int) {

  val myListenerShards: mutable.Map[Int, WebsocketListener] = mutable.Map()

  lazy val system: ActorSystem = ActorSystem("DiscordScalaWebsocketSharding")

  def addListener(l: WebsocketListener): Unit = {
    val m = (myListenerShards + (-1 -> null)).maxBy(_._1)._1
    if (m >= max_shards) throw new OutOfShardsException
    myListenerShards += (m + 1 -> l)
  }

}

class OutOfShardsException extends Exception("Out of shards. Specify a higher maximum shards!")