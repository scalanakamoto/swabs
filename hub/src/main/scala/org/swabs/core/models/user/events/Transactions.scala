package org.swabs.core.models.user.events

import cats.implicits._
import org.swabs.core.models.user.Currency.Currency
import org.swabs.util.GlobalDateTimeFormat
import org.swabs.util.Parse
import org.swabs.util.Validate
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsPath
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

import java.time.LocalDateTime

object Transactions {
  final case class Transaction(
      dateTime: TransactionDateTime,
      amount: TransactionAmount,
      currency: Currency,
      note: Note
  )

  object Transaction {
    implicit val writes: Writes[Transaction] = Json.writes[Transaction]
    implicit val reads: Reads[Transaction] = (
      (JsPath \ "dateTime").read[TransactionDateTime] and
      (JsPath \ "amount").read[TransactionAmount] and
      (JsPath \ "currency").read[Currency] and
      (JsPath \ "note").read[Note]
    )(Transaction.apply _)
  }

  final case class TransactionDateTime(value: LocalDateTime) extends AnyVal

  object TransactionDateTime {
    implicit val parse: Parse[TransactionDateTime] =
      Parse.localDateTimeParser(GlobalDateTimeFormat.apply).map(TransactionDateTime.apply)
    implicit val reads: Reads[TransactionDateTime] = Parse.reads
    implicit val writes: Writes[TransactionDateTime] =
      Writes[LocalDateTime](dt => JsString(GlobalDateTimeFormat.apply.format(dt))).contramap(_.value)
  }

  final case class TransactionAmount(value: Double) extends AnyVal

  object TransactionAmount {
    private val fnParse: Double => Double = BigDecimal(_).setScale(15, BigDecimal.RoundingMode.HALF_UP).doubleValue
    implicit val reads: Reads[TransactionAmount] = Reads.of[Double].map(fnParse).map(TransactionAmount.apply)
    implicit val writes: Writes[TransactionAmount] = Writes.of[Double].contramap(_.value)
  }

  final case class Note(value: String) extends AnyVal

  object Note {
    private val regex = """^[a-zA-Z0-9!+*/.,\s-]*$""".r
    implicit val validate: Validate[Note] = (Validate.matches(regex) |+| Validate.maxLength(300)).contramap(_.value)
    implicit val parse: Parse[Note] = Parse.stringParse.map(Note.apply).validate
    implicit val reads: Reads[Note] = Parse.reads
    implicit val writes: Writes[Note] = Writes.of[String].contramap(_.value)
  }
}
