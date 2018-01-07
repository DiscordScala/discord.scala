package github.discordscala.core.event

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Client => _, _}
import github.discordscala.core._
import github.discordscala.core.event.opnonzero.{HeartbeatEvent, HelloEvent, HelloEventBase}
import github.discordscala.core.event.opzero.{MessageCreateEvent, MessageCreateEventBase}
import github.discordscala.core.event.payload.{GatewayIdentificationData, IdentifyPayload}
import github.discordscala.core.util.EventListener
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

  implicit val system: ActorSystem = ActorSystem("WebsocketListenerSystem")
  implicit val materializer: Materializer = ActorMaterializer()

  sharding.myListenerShards += (shard.shardNumber -> this)

  lazy val req = WebSocketRequest(c.gatewayURL)

  lazy val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 1000, OverflowStrategy.fail)
  lazy val messageSink: Sink[Message, NotUsed] = Flow[Message].map(handleGateway).to(Sink.ignore)

  var heartbeatThread: Thread = _
  var lastSequence: Option[Int] = None

  var currentRequest: (ActorRef, (Future[WebSocketUpgradeResponse], UniqueKillSwitch)) = _

  val requiredHandlers: Traversable[EventListener[_ <: WebsocketEvent]] = Seq(
    new EventListener[HelloEvent] {
      override def base: WebsocketEventBase[HelloEvent] = HelloEventBase
      override def apply(e: HelloEvent): Unit = {
        println("hello handled")
        heartbeatThread = new Thread(() => {
          while (true) {
            currentRequest._1.heartbeat(HeartbeatEvent(lastSequence))
            Thread.sleep(e.d.heartbeatInterval.toLong)
          }
        })
        heartbeatThread.start()
        currentRequest._1.identify()
      }
    },
    new EventListener[MessageCreateEvent] {
      override def base: WebsocketEventBase[MessageCreateEvent] = MessageCreateEventBase
      override def apply(e: MessageCreateEvent): Unit = {
        println(s"message created, $e")
      }
    },
  )

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
    val js = m.asTextMessage.getStrictText
    println(s"json is $js")
    val jast = correctInput(parse(js))
    val web = jast \ "op" match {
      case JInt(b) if b == BigInt(0) => println("zmap")
        jast \ "t" match {
          case JString(e) => println(s"zmap got $e"); WebsocketListener.opZeroMap(e)
        }
      case JInt(b) => println(s"nzmap got $b, ${WebsocketListener.opNonZeroMap.size}"); val a = WebsocketListener.opNonZeroMap(b.toInt); println(s"got $a"); a
    }
    println(HelloEventBase)
    println(web.eventOp)
    val completeSet = c.eventHandlers ++ requiredHandlers
    println("completed set")
    println(compactRender(jast \ "d"))
    val event = web(jast \ "d", c, this)
    println(s"starting iter, ${completeSet.size}")
    completeSet.foreach {
      case listener if listener.base == web => println("correct listener"); listener(event.asInstanceOf[listener.Event])
      case _ => println("not a correct listener")
    }
    println("done with handling")
  }

  implicit class GatewayWebsocket(ar: ActorRef) {

    def identify(): Future[Unit] = submit(Extraction.decompose(IdentifyPayload(GatewayIdentificationData(c.token, shard = Array(sharding.myListenerShards.find(_._2 == WebsocketListener.this).get._1, sharding.max_shards)))))

    def heartbeat(e: HeartbeatEvent): Future[Unit] = submit(Extraction.decompose(e))

    def submit(j: JValue): Future[Unit] = Future {
      val corrected = j transformField {
        case JField(key, value) => JField(keyDeCorrectionReg.replaceAllIn(key, (m) => s"_${m.matched.toLowerCase}"), value)
      }
      println(s"sending ${compactRender(j)}")
      ar ! TextMessage(compactRender(j))
    }

  }

}

object WebsocketListener {

  lazy val allBases: TraversableOnce[WebsocketEventBase[_ <: WebsocketEvent]] = {
    val classFinder = ClassFinder()
    val classes = classFinder.getClasses()
    val eventBases = ClassFinder.concreteSubclasses("github.discordscala.core.event.WebsocketEventBase", classes)
    eventBases.filter(_.name.endsWith("$class"))
    val rm = ru.runtimeMirror(ClassLoader.getSystemClassLoader)
    eventBases.map((ci) => rm.reflectModule(rm.staticModule(ci.name)).instance.asInstanceOf[WebsocketEventBase[_ <: WebsocketEvent]])
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

  lazy val system: ActorSystem = ActorSystem("DiscordScalaWebsocketSharding")

  def addListener(l: WebsocketListener): Unit = {
    val m = (myListenerShards + (-1 -> null)).maxBy(_._1)._1
    if (m >= max_shards) throw new OutOfShardsException
    myListenerShards += (m + 1 -> l)
  }

}

class OutOfShardsException extends Exception("Out of shards. Specify a higher maximum shards!")