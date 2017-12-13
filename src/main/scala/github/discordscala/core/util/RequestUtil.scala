package github.discordscala.core.util

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json._
import github.discordscala.core._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object RequestUtil {

  implicit val system = ActorSystem("DiscordScalaHTTPRequests")
  implicit val materializer = ActorMaterializer()

  def restRequestFuture(url: String, headers: Map[String, String], method: RequestMethod = Get, body: Option[(String, String)] = None): Future[JValue] = Future {
    var status = 0
    var strResponse: String = null
    do {
      val brequest = (method match {
        case Get => sttp.get(uri"$url")
        case Post => sttp.post(uri"$url")
        case Patch => sttp.patch(uri"$url")
      }).headers(headers)
      val request = (body match {
        case Some((mime, content)) => brequest.contentType(mime, "UTF-8").streamBody(Source.single(ByteString(content, "UTF-8")))
        case None => brequest
      }).response(asStream[Source[ByteString, NotUsed]])
      val waitResponse = request.send()
      val response = Await.result(waitResponse, Duration.Inf)
      status = response.code
      if(status == 429) {
        val unRatelimitTime = response.header("X-RateLimit-Reset").get.toLong
        Thread.sleep(System.currentTimeMillis() - (unRatelimitTime * 1000 + 500))
      } else {
        if(status / 100 != 2) {
          throw new RuntimeException(s"Status $status") // FIXME create better error reporting for thi
        } else {
          strResponse = response.body match {
            case Left(str) => str
            case Right(sor) => Await.result(sor.runFold("")((acc, ns) => acc + ns.foldLeft("")((s, b) => s + b.toChar.toString)), Duration.Inf)
          }
        }
      }
    } while (status == 429)
    parse(strResponse)
  }(executor = ExecutionContext.global)

  def restRequestFuture(url: String, headers: Map[String, String], method: RequestMethod, body: JValue): Future[JValue] = restRequestFuture(url, headers, method, Some(("application/json", compactRender(body))))

  def awaitRestRequestFuture(url: String, headers: Map[String, String], method: RequestMethod = Get, body: Option[(String, String)] = None, timeout: Duration = Duration.Inf): JValue = Await.result(restRequestFuture(url, headers), timeout)

  def awaitRestRequestFuture(url: String, headers: Map[String, String], method: RequestMethod, body: JValue, timeout: Duration): JValue = awaitRestRequestFuture(url, headers, method, Some(("application/json", compactRender(body))), timeout)

}

sealed trait RequestMethod
case object Get extends RequestMethod
case object Post extends RequestMethod
case object Patch extends RequestMethod