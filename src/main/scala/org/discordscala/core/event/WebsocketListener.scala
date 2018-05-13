package org.discordscala.core.event

import java.util.zip.Inflater

import akka.NotUsed
import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Client => _, _}
import net.liftweb.json._
import org.clapper.classutil.ClassFinder
import org.discordscala.core._
import org.discordscala.core.event.opnonzero.{HeartbeatEvent, HelloEvent}
import org.discordscala.core.event.payload.{GatewayIdentificationData, IdentifyPayload}

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.math.BigInt
import scala.reflect.runtime.{universe => ru}
import scala.util.matching.Regex

class WebsocketListener(val c: Client, val shard: Shard)(implicit sharding: Sharding) {

  lazy val req = WebSocketRequest(c.gatewayURL)
  lazy val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 1000, OverflowStrategy.fail)
  lazy val messageSink: Sink[Message, NotUsed] = Flow[Message].map(handleGateway).to(Sink.ignore)
  lazy val requiredHandlers: PartialFunction[WebsocketEvent, Unit] = {
    case HelloEvent(d) =>
      heartbeatThread = new Thread(() => {
        while (true) {
          currentRequest._1.heartbeat(HeartbeatEvent(lastSequence))
          Thread.sleep(d.heartbeatInterval.toLong)
        }
      })
      heartbeatThread.start()
      currentRequest._1.identify()
  }

  implicit val materializer: Materializer = ActorMaterializer()

  sharding.myListenerShards += (shard.shardNumber -> this)
  val keyCorrectionReg: Regex = """_([a-z])""".r
  val keyDeCorrectionReg: Regex = """([A-Z])""".r
  var heartbeatThread: Thread = _
  var lastSequence: Option[Int] = None
  var currentRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = _

  def submit(v: JValue): Unit = {
    currentRequest._1.submit(v)
  }

  def start(): Unit = {
    currentRequest = startRequest
    Await.result(currentRequest._2._1, Duration.Inf)
  }

  def startRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = {
    val r = Http().webSocketClientFlow(req)
    messageSource.viaMat(r.viaMat(KillSwitches.single)(Keep.both))(Keep.both).toMat(messageSink)(Keep.left).run()
  }

  def stop(): Unit = {
    currentRequest._2._2.shutdown()
    currentRequest = null
    heartbeatThread.interrupt()
    heartbeatThread = null
    lastSequence = None
  }

  def handleGateway(m: Message): Future[Unit] = Future {
    try {
      val js = try {
        if(m.asTextMessage.isStrict) {
          m.asTextMessage.getStrictText
        } else {
          m.asTextMessage.getStreamedText.runFold[String]("", (s1, s2) => {
            s1 + s2
          }, materializer).toCompletableFuture.get()
        }
      } catch {
        case _: IllegalStateException =>
          val bs = m.asBinaryMessage.getStrictData
          val inf = new Inflater()
          inf.setInput(bs.asByteBuffer.array())
          val result = Array.fill(2048)(0.toByte)
          val len = inf.inflate(result)
          new String(result, 0, len, "UTF-8")
      }
      val jast = correctInput(parse(js))
      println(compactRender(jast))
      val s = jast \ "s"
      s match {
        case JInt(n) => lastSequence = Some(n.intValue())
        case _ =>
      }
      val web = jast \ "op" match {
        case JInt(b) if b == BigInt(0) =>
          jast \ "t" match {
            case JString(e) => WebsocketListener.opZeroMap.get(e)
          }
        case JInt(b) => WebsocketListener.opNonZeroMap.get(b.toInt)
      }
      web match {
        case Some(nob) =>
          val event = nob(jast \ "d", c, this)
          if (requiredHandlers.isDefinedAt(event)) requiredHandlers(event)
          if (c.handler.isDefinedAt(event)) c.handler(event)
        case None =>
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def correctInput(v: JValue): JValue = v transformField {
    case JField(key, value) => JField(keyCorrectionReg.replaceAllIn(key, m => m.group(1).toUpperCase), value)
  }

  implicit class GatewayWebsocket(ar: ActorRef) {

    def identify(): Future[Unit] = submit(Extraction.decompose(IdentifyPayload(GatewayIdentificationData(c.token, shard = Array(sharding.myListenerShards.find(_._2 == WebsocketListener.this).get._1, sharding.max_shards)))))

    def submit(j: JValue): Future[Unit] = Future {
      val corrected = j transformField {
        case JField(key, value) => JField(keyDeCorrectionReg.replaceAllIn(key, m => s"_${m.matched.toLowerCase}"), value)
      }
      println(compactRender(corrected))
      ar ! TextMessage(compactRender(corrected))
    }

    def heartbeat(e: HeartbeatEvent): Future[Unit] = submit(Extraction.decompose(e))

  }

}

object WebsocketListener {

  lazy val allBases: Traversable[WebsocketEventBase[_ <: WebsocketEvent]] = {
    val classFinder = ClassFinder()
    val classes = classFinder.getClasses()
    val eventBases = ClassFinder.concreteSubclasses("org.discordscala.core.event.WebsocketEventBase", classes)
    eventBases.filter(_.name.endsWith("$class"))
    val rm = ru.runtimeMirror(ClassLoader.getSystemClassLoader)
    eventBases.map(ci => rm.reflectModule(rm.staticModule(ci.name)).instance.asInstanceOf[WebsocketEventBase[_ <: WebsocketEvent]]).toSeq
  }

  lazy val opZeroMap: Map[String, WebsocketEventBase[_ <: WebsocketEvent]] = {
    val zeroBases = allBases.filter(_.eventOp == 0)
    zeroBases.map(b => (b.eventName.get, b)).toMap
  }
  lazy val opNonZeroMap: Map[Int, WebsocketEventBase[_ <: WebsocketEvent]] = {
    val zeroBases = allBases.filter(_.eventOp != 0)
    zeroBases.map(b => (b.eventOp, b)).toMap
  }

}

case class Sharding(max_shards: Int) {

  val myListenerShards: mutable.Map[Int, WebsocketListener] = mutable.Map()

  def addListener(l: WebsocketListener): Unit = {
    val m = (myListenerShards + (-1 -> null)).maxBy(_._1)._1
    if (m >= max_shards) throw new OutOfShardsException
    myListenerShards += (m + 1 -> l)
  }

}

class OutOfShardsException extends Exception("Out of shards. Specify a higher maximum shards!")