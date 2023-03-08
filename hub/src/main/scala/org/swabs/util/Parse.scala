package org.swabs.util

import cats.Functor
import cats.SemigroupK
import cats.implicits._
import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.Reads

import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

trait Parse[T] {
  def parse(s: String): Either[String, T]

  def validate(implicit validate: Validate[T]): Parse[T] = parse(_).flatMap(validate.validate)

  def withError(errorMsg: String): Parse[T] = parse(_).leftMap(_ => errorMsg)
}

object Parse {

  private val numbersPattern = "^\\s*(-?\\d+)\\s*$".r

  private val floatPattern = "^\\s*([+-]?([0-9]*[.])?[0-9]+)\\s*$".r

  implicit val functor: Functor[Parse] = new Functor[Parse] {
    override def map[A, B](fa: Parse[A])(f: A => B): Parse[B] = fa.parse(_).map(f(_))
  }

  implicit val semigroupK: SemigroupK[Parse] = new SemigroupK[Parse] {
    override def combineK[A](x: Parse[A], y: Parse[A]): Parse[A] = s => x.parse(s) <+> y.parse(s)
  }

  def parse[T](s: String)(implicit parse: Parse[T]): Either[String, T] = parse.parse(s)

  def uuidParse: Parse[UUID] = uuid => Try(UUID.fromString(uuid)).fold(_ => Left("parse.expected.uuid"), Right(_))

  def localDateTimeParser(formatter: DateTimeFormatter): Parse[LocalDateTime] = s =>
    Try(formatter.parse(s)).fold(_ => Left("parse.expected.localdatetime"), Right(_)).map(LocalDateTime.from)

  def yearParser: Parse[Year] = year => Try(Year.of(year.toInt)).toEither.leftMap(_ => "parse.expected.year")

  def intParse: Parse[Int] = {
    case numbersPattern(s) =>
      Try(s.toInt).toEither.leftMap(_ => "parse.expected.int")
    case _ =>
      Left("parse.expected.int")
  }

  def longParse: Parse[Long] = {
    case numbersPattern(s) =>
      Try(s.toLong).toEither.leftMap(_ => "parse.expected.long")
    case _ =>
      Left("parse.expected.long")
  }

  def decimalParse: Parse[BigDecimal] = s =>
    Try(BigDecimal(s)).toEither.leftMap(_ => "parse.expected.bigDecimal")

  def doubleParse: Parse[Double] = {
    case floatPattern(s, _) =>
      Right(s.toDouble).flatMap(n => Either.cond(test = !n.isInfinite, right = n, left = "parse.expected.double"))
    case _ =>
      Left("parse.expected.double")
  }

  def stringParse: Parse[String] = Right(_)

  def enumNameParse[E <: Enumeration](`enum`: E): Parse[E#Value] = o =>
    Try(`enum`.withName(o)).fold(_ => Left("parse.expected.enum"), Right(_))

  def reads[T](implicit parse: Parse[T]): Reads[T] = Reads[T] {
    case JsString(s) => parse.parse(s).fold(JsError(_), JsSuccess(_))
    case _           => JsError("expected.string")
  }

  def of[T](implicit parse: Parse[T]): Parse[T] = parse.parse

}
