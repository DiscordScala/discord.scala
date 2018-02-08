package github.discordscala.core.event

import akka.NotUsed
import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Client => _, _}
import github.discordscala.core._
import github.discordscala.core.event.opnonzero.{HeartbeatEvent, HelloEvent}
import github.discordscala.core.event.payload.{GatewayIdentificationData, IdentifyPayload}
import net.liftweb.json._
import org.clapper.classutil.ClassFinder

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.math.BigInt
import scala.reflect.runtime.{universe => ru}
import scala.util.matching.Regex

class WebsocketListener(val c: Client, val shard: Shard)(implicit sharding: Sharding) {

  val keyCorrectionReg: Regex = """_([a-z])""".r
  val keyDeCorrectionReg: Regex = """([A-Z])""".r

  def submit(v: JValue): Unit = {
    currentRequest._1.submit(v)
  }

  def correctInput(v: JValue): JValue = v transformField {
    case JField(key, value) => JField(keyCorrectionReg.replaceAllIn(key, (m) => m.group(1).toUpperCase), value)
  }

  implicit val materializer: Materializer = ActorMaterializer()

  sharding.myListenerShards += (shard.shardNumber -> this)

  lazy val req = WebSocketRequest(c.gatewayURL)

  lazy val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 1000, OverflowStrategy.fail)
  lazy val messageSink: Sink[Message, NotUsed] = Flow[Message].map(handleGateway).to(Sink.ignore)

  var heartbeatThread: Thread = _
  var lastSequence: Option[Int] = None

  var currentRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = _

  lazy val requiredHandlers: Traversable[ActorRef] = {
    class HelloHandler() extends Actor {
      def receive: PartialFunction[Any, Unit] = {
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
    }
    try {
      val ref = system.actorOf(Props(new HelloHandler), "helloHandler")
      Seq(ref)
    } catch {
      case e: Exception => e.printStackTrace(); Seq()
    }
  }

  def startRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = {
    val r = Http().webSocketClientFlow(req)
    messageSource.viaMat(r.viaMat(KillSwitches.single)(Keep.both))(Keep.both).toMat(messageSink)(Keep.left).run()
  }

  def start(): Unit = {
    currentRequest = startRequest
    Await.result(currentRequest._2._1, Duration.Inf)
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
      val js = m.asTextMessage.getStrictText
      val jast = correctInput(parse(js))
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
          val completeSet = requiredHandlers ++ c.eventHandlers
          val event = nob(jast \ "d", c, this)
          completeSet.foreach((s) => {
            try {
              s ! event
            } catch {
              case e: Exception => e.printStackTrace()
            }
          })
        case None =>
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  implicit class GatewayWebsocket(ar: ActorRef) {

    def identify(): Future[Unit] = submit(Extraction.decompose(IdentifyPayload(GatewayIdentificationData(c.token, shard = Array(sharding.myListenerShards.find(_._2 == WebsocketListener.this).get._1, sharding.max_shards)))))

    def heartbeat(e: HeartbeatEvent): Future[Unit] = submit(Extraction.decompose(e))

    def submit(j: JValue): Future[Unit] = Future {
      val corrected = j transformField {
        case JField(key, value) => JField(keyDeCorrectionReg.replaceAllIn(key, (m) => s"_${m.matched.toLowerCase}"), value)
      }
      ar ! TextMessage(compactRender(j))
    }

  }

}

object WebsocketListener {

  lazy val allBases: Traversable[WebsocketEventBase[_ <: WebsocketEvent]] = {
    val classFinder = ClassFinder()
    val classes = classFinder.getClasses()
    val eventBases = ClassFinder.concreteSubclasses("github.discordscala.core.event.WebsocketEventBase", classes)
    eventBases.filter(_.name.endsWith("$class"))
    val rm = ru.runtimeMirror(ClassLoader.getSystemClassLoader)
    eventBases.map((ci) => rm.reflectModule(rm.staticModule(ci.name)).instance.asInstanceOf[WebsocketEventBase[_ <: WebsocketEvent]]).toSeq
  }

  lazy val opZeroMap: Map[String, WebsocketEventBase[_ <: WebsocketEvent]] = {
    val zeroBases = allBases.filter(_.eventOp == 0)
    zeroBases.map((b) => (b.eventName.get, b)).toMap
  }
  lazy val opNonZeroMap: Map[Int, WebsocketEventBase[_ <: WebsocketEvent]] = {
    val zeroBases = allBases.filter(_.eventOp != 0)
    zeroBases.map((b) => (b.eventOp, b)).toMap
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