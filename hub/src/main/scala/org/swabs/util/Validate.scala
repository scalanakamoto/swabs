package org.swabs.util

import cats.Contravariant
import cats.Monoid
import cats.implicits.catsSyntaxFlatMapOps
import play.api.libs.json.Reads

import scala.util.matching.Regex

trait Validate[T] {
  def validate(s: T): Either[String, T]
}

object Validate {

  def reads[T](reads: Reads[T])(implicit validate: Validate[T]): Reads[T] =
    reads.flatMap(t => validate.validate(t).fold(Reads.failed(_), Reads.pure(_)))

  def cond[T](f: T => Boolean): Validate[T] = t => Either.cond(test = f(t), right = t, left = "validate.pattern")

  val matches: Regex => Validate[String] = regex => strValue => Either.cond(
    test = regex.matches(strValue),
    right = strValue,
    left = "validate.pattern"
  )

  val maxLength: Int => Validate[String] = max => strValue => Either.cond(
    test = strValue.length <= max,
    right = strValue,
    left = "validate.length.max"
  )

  implicit def monoid[T]: Monoid[Validate[T]] = new Monoid[Validate[T]] {
    override def empty: Validate[T] = x => Right(x)
    override def combine(x: Validate[T], y: Validate[T]): Validate[T] = x.validate(_).flatMap(y.validate)
  }

  implicit val cofunctor: Contravariant[Validate] = new Contravariant[Validate] {
    override def contramap[A, B](fa: Validate[A])(f: B => A): Validate[B] = b => fa.validate(f(b)) >> Right(b)
  }
}
