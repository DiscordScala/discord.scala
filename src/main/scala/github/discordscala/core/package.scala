package github.discordscala

import java.time.Instant

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import github.discordscala.core.models.Snowflaked
import github.discordscala.core.serializers.SnowflakeSerializer
import net.liftweb.json.{DefaultFormats, Formats}
import spire.math.ULong

import scala.concurrent.Future

package object core {

  val discordEpoch = ULong(1420070400000l)
  val userAgent: (String, String) = "User-Agent" -> "discord.scala/0.1"
  implicit val backend: SttpBackend[Future, Source[ByteString, Any]] =  AkkaHttpBackend()
  implicit val formats: Formats = DefaultFormats + SnowflakeSerializer

  implicit class SnowflakeUtil(s: Snowflaked) {

    def toTime: Option[Instant] = {
      s.id match {
        case Some(id) =>
          val depoch = id >> 22
          val uepoch = depoch + discordEpoch
          Some(Instant.ofEpochMilli(uepoch.signed))
        case None => None
      }
    }

  }

}
