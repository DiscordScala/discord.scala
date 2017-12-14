package github.discordscala

import java.time.Instant

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import github.discordscala.core.models.snowflake.Snowflaked
import github.discordscala.core.serializers.{SnowflakeSerializer, USnowflakeSerializer}
import net.liftweb.json.{DefaultFormats, Formats}
import spire.math.ULong

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

package object core {

  /**
    * Start of the Discord Epoch
    */
  val discordEpoch = ULong(1420070400000l)
  /**
    * Standard Discord.Scala Useragent
    */
  val userAgent: (String, String) = "User-Agent" -> "discord.scala/0.1"
  implicit val executor: ExecutionContextExecutor = ExecutionContext.global
  implicit val backend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val formats: Formats = DefaultFormats + SnowflakeSerializer + USnowflakeSerializer

  implicit class SnowflakeUtil(s: Snowflaked) {

    /**
      * Converts a snowflake to an Instant
      *
      * @return Approximate creation time of the snowflake
      */
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
