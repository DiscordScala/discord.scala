package github.discordscala.core.util

import java.lang.{StringBuilder => JLSBuilder}

import scala.StringContext.treatEscapes
import scala.collection.mutable.WrappedArray
import scala.collection.immutable.Nil

object Logger {

  implicit class LogContext(private val stringContext: StringContext) {
    def error(args: Any*): String = {
      "\u001B[31m[ERROR]\u001B[30m " + interpolate(args)
    }

    private def interpolate(wrappedArgs: Any*): String = {
      val args = wrappedArgs match {
        case y if y.isInstanceOf[WrappedArray[Nil.type]] => WrappedArray.empty
        case x if x.isInstanceOf[WrappedArray[WrappedArray[Any]]] => x.asInstanceOf[WrappedArray[WrappedArray[Any]]](0)
        case _ => WrappedArray.empty
      }

      val parts = stringContext.parts

      def checkLengths(args: Seq[Any]): Unit =
        if (parts.length != args.length + 1)
          throw new IllegalArgumentException("wrong number of arguments ("+ args.length
            +") for interpolated string with "+ parts.length +" parts")

      checkLengths(args)
      val pi = parts.iterator
      val ai = args.iterator
      val bldr = new JLSBuilder(treatEscapes(pi.next()))
      while (ai.hasNext) {
        bldr append ai.next
        bldr append treatEscapes(pi.next())
      }
      bldr.toString
    }
  }

}
