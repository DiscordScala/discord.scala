package org.discordscala.core.util

import shapeless._

trait Overlay[A] {
  def apply(a: A, b: A): A
}

object Overlay {

  implicit val HNilOverlay: Overlay[HNil] = (a: HNil, b: HNil) => HNil

  implicit def HConsOverlay[H, T <: HList](implicit ev: Overlay[T]): Overlay[Option[H] :: T] =
    (a: Option[H] :: T, b: Option[H] :: T) => (a.head orElse b.head) :: ev(a.tail, b.tail)

  implicit def GenericOverlay[A, B <: HList](implicit ga: Generic.Aux[A, B], ob: Overlay[B]): Overlay[A] =
    (a: A, b: A) => ga.from(ob(ga.to(a), ga.to(b)))

}