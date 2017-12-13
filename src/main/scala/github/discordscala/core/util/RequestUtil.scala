package github.discordscala.core.util

import java.net.{HttpURLConnection, URL}

import net.liftweb.json.JsonAST.JValue
import net.liftweb.json._

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.io.Source

object RequestUtil {

  def restRequestFuture(url: String, headers: Map[String, String]): Future[JValue] = Future {
    val u = new URL(url)
    var status = 0
    var c: HttpURLConnection = null
    do {
      c = u.openConnection().asInstanceOf[HttpURLConnection]
      headers.foreach((t) => c.addRequestProperty(t._1, t._2))
      if(status == 429) {
        Thread.sleep(c.getHeaderField("X-RateLimit-Reset").toLong - ((System.currentTimeMillis() + 500) / 1000))
      }
      c.connect()
      status = c.getResponseCode
      c.disconnect()
    } while (status / 100 != 2)
    parse(Source.fromInputStream(c.getInputStream).getLines().mkString("\n"))
  }

  def awaitRestRequestFuture(url: String, headers: Map[String, String], timeout: Duration = Duration.Inf): JValue = restRequestFuture(url, headers).result(timeout)

}
