package org.swabs.core.models.money

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json._

sealed trait Money extends Product with Serializable

object Money {
  final case class BTC(value: Double) extends Money
  object BTC {
    implicit val reads: Reads[BTC] = (
      (__ \ "currency").read[String].filter(_ == "BTC") and
      (__ \ "amount").read[Double].map(parseDouble(8))
    )((_, o2) => BTC(o2))

    implicit val writes: Writes[BTC] = (
      (__ \ "currency").write[String] and
      (__ \ "amount").write[Double]
    )(o => "BTC" -> o.value)
  }

  final case class SATS(value: Long) extends Money
  object SATS {
    implicit val reads: Reads[SATS] = (
      (__ \ "currency").read[String].filter(_ == "SATS") and
        (__ \ "amount").read[Long]
      )((_, o2) => SATS(o2))

    implicit val writes: Writes[SATS] = (
      (__ \ "currency").write[String] and
        (__ \ "amount").write[Long]
      )(o => "SATS" -> o.value)
  }

  final case class XMR(value: Double) extends Money
  object XMR {
    implicit val reads: Reads[XMR] = (
      (__ \ "currency").read[String].filter(_ == "XMR") and
        (__ \ "amount").read[Double].map(parseDouble(15))
      )((_, o2) => XMR(o2))

    implicit val writes: Writes[XMR] = (
      (__ \ "currency").write[String] and
        (__ \ "amount").write[Double]
      )(o => "XMR" -> o.value)
  }

  sealed trait FIAT {
    val parsed: Double => Double = parseDouble(2)
  }

  final case class USD(value: Double) extends Money
  object USD extends FIAT {
    implicit val reads: Reads[USD] = (
      (__ \ "currency").read[String].filter(_ == "USD") and
        (__ \ "amount").read[Double].map(parsed)
      )((_, o2) => USD(o2))

    implicit val writes: Writes[USD] = (
      (__ \ "currency").write[String] and
        (__ \ "amount").write[Double]
      )(o => "USD" -> o.value)
  }

  final case class EUR(value: Double) extends Money
  object EUR extends FIAT {
    implicit val reads: Reads[EUR] = (
      (__ \ "currency").read[String].filter(_ == "EUR") and
        (__ \ "amount").read[Double].map(parsed)
      )((_, o2) => EUR(o2))

    implicit val writes: Writes[EUR] = (
      (__ \ "currency").write[String] and
        (__ \ "amount").write[Double]
      )(o => "EUR" -> o.value)
  }

  final case class CHF(value: Double) extends Money
  object CHF extends FIAT {
    implicit val reads: Reads[CHF] = (
      (__ \ "currency").read[String].filter(_ == "CHF") and
        (__ \ "amount").read[Double].map(parsed)
      )((_, o2) => CHF(o2))

    implicit val writes: Writes[CHF] = (
      (__ \ "currency").write[String] and
        (__ \ "amount").write[Double]
      )(o => "CHF" -> o.value)
  }

  implicit val reads: Reads[Money] =
    __.read[BTC].widen[Money] orElse
    __.read[SATS].widen[Money] orElse
    __.read[XMR].widen[Money] orElse
    __.read[USD].widen[Money] orElse
    __.read[EUR].widen[Money] orElse
    __.read[CHF].widen[Money]

  implicit val writes: Writes[Money] = {
    case o: BTC => Json.toJson(o)
    case o: SATS => Json.toJson(o)
    case o: XMR => Json.toJson(o)
    case o: USD => Json.toJson(o)
    case o: EUR => Json.toJson(o)
    case o: CHF => Json.toJson(o)
  }

  private def parseDouble(scale: Int)(value: Double) =
    BigDecimal(value).setScale(scale, BigDecimal.RoundingMode.HALF_UP).doubleValue
}
