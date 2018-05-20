package org.discordscala.core.util

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json._
import org.discordscala.core._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.matching.Regex

/**
  * Object that does the heavy lifting for API requests
  */
object RequestUtil {

  val keyCorrectionReg: Regex = """_([a-z])""".r
  val keyDeCorrectionReg: Regex = """([A-Z])""".r

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  /**
    * Method that makes a REST request
    *
    * @param url     URL to request to
    * @param headers Map of Headers
    * @param method  REST Method to use
    * @param body    JSON representation of the body
    * @return Future of the request
    */
  def restRequestFuture(url: String, headers: Map[String, String], method: RequestMethod, body: JValue): Future[Either[DiscordException, JValue]] = restRequestFuture(url, headers, method, Some(("application/json", compactRender(body transformField {
    case JField(key, value) => JField(keyDeCorrectionReg.replaceAllIn(key, m => s"_${m.matched.toLowerCase}"), value)
  }))))

  /**
    * Method that makes a REST request
    *
    * @param url     URL to request to
    * @param headers Map of Headers
    * @param method  REST method to use
    * @param body    Body of the request, with type (type, body)
    * @return Future of the request
    */
  def restRequestFuture(url: String, headers: Map[String, String], method: RequestMethod = RequestMethod.Get, body: Option[(String, String)] = None): Future[Either[DiscordException, JValue]] = Future {
    var status = 0
    var eResponse: Either[DiscordException, String] = null
    do {
      val brequest = (method match {
        case RequestMethod.Get => sttp.get(uri"$url")
        case RequestMethod.Post => sttp.post(uri"$url")
        case RequestMethod.Patch => sttp.patch(uri"$url")
        case RequestMethod.Delete => sttp.delete(uri"$url")
      }).headers(headers + ("User-Agent" -> s"$userAgentName/$userAgentVersion"))
      val request = (body match {
        case Some((mime, content)) => brequest.contentType(mime, "UTF-8").streamBody(Source.single(ByteString(content, "UTF-8")))
        case None => brequest
      }).response(asStream[Source[ByteString, NotUsed]])
      val waitResponse = request.send()
      val response = Await.result(waitResponse, Duration.Inf)
      status = response.code
      if (status == 429) {
        val unRatelimitTime = response.header("X-RateLimit-Reset").get.toLong
        Thread.sleep(System.currentTimeMillis() - (unRatelimitTime * 1000 + 500))
      } else {
        if (status / 100 == 4 || status / 100 == 5) {
          eResponse = Left(status match {
            case 400 => DiscordException.BadRequest
            case 401 => DiscordException.Unauthorized
            case 403 => DiscordException.Forbidden
            case 404 => DiscordException.NotFound
            case 502 => DiscordException.BadRequest
          })
        } else {
          eResponse = Right(response.body match {
            case Left(str) => str
            case Right(sor) => Await.result(sor.runFold("")((acc, ns) => acc + ns.foldLeft("")((s, b) => s + b.toChar.toString)), Duration.Inf)
          })
        }
      }
    } while (status == 429)
    eResponse match {
      case Left(e) => Left(e)
      case Right(s) => Right(parse(s) transformField {
        case JField(key, value) => JField(keyCorrectionReg.replaceAllIn(key, m => m.group(1).toUpperCase), value)
      })
    }
  }(executor = ExecutionContext.global)

  /**
    * Like restRequestFuture, but waits for the result
    *
    * @param url     URL to request to
    * @param headers Map of Headers
    * @param method  REST Method to use
    * @param body    JSON representation of the body
    * @param timeout How long to wait for an Answer
    * @return Either a Discord Exception or the response
    */
  def awaitRestRequestFuture(url: String, headers: Map[String, String], method: RequestMethod, body: JValue, timeout: Duration): Either[DiscordException, JValue] = awaitRestRequestFuture(url, headers, method, Some(("application/json", compactRender(body transformField {
    case JField(key, value) => JField(keyDeCorrectionReg.replaceAllIn(key, m => s"_${m.matched.toLowerCase}"), value)
  }))), timeout)

  /**
    * Like restRequestFuture, but waits for the result
    *
    * @param url     URL to request to
    * @param headers Map of Headers
    * @param method  REST Method to use
    * @param body    Body of the request, with type (type, body)
    * @param timeout How long to wait for an Answer
    * @return Either a Discord Exception or the response
    */
  def awaitRestRequestFuture(url: String, headers: Map[String, String], method: RequestMethod = RequestMethod.Get, body: Option[(String, String)] = None, timeout: Duration = Duration.Inf): Either[DiscordException, JValue] = Await.result(restRequestFuture(url, headers, method, body), timeout)

}

/**
  * Base trait for request methods
  */
sealed trait RequestMethod

object RequestMethod {

  /**
    * GET Request
    */
  case object Get extends RequestMethod

  /**
    * POST request
    */
  case object Post extends RequestMethod

  /**
    * PATCH request
    */
  case object Patch extends RequestMethod

  /**
    * DELETE request
    */
  case object Delete extends RequestMethod

}

/**
  * Exceptions that can occur when making connections
  */
sealed trait DiscordException

object DiscordException {

  case object BadRequest extends DiscordException

  /**
    * Effectively a 401
    */
  case object Unauthorized extends DiscordException

  /**
    * Effectively a 403
    */
  case object Forbidden extends DiscordException

  /**
    * Effectively a 404
    */
  case object NotFound extends DiscordException

  case object GatewayUnavailable extends DiscordException

  case object CombinedOrUnknown extends DiscordException

}