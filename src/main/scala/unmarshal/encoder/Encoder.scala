package unmarshal.encoder

import unmarshal.model.Json
import unmarshal.model.Json.{JsonArray, JsonBool, JsonNull, JsonNum, JsonObject, JsonString}

import scala.compiletime.{constValue, erasedValue, summonInline}
import scala.deriving.Mirror

trait Encoder[A] {
  def toJson(value: A): Json
}

object Encoder {

  def apply[A](using
    encoder: Encoder[A]
  ): Encoder[A] = encoder

  inline given autoDerive[A <: Product](using
    m: Mirror.Of[A]
  ): Encoder[A] = (value: A) => {
    val fieldNames       = getFieldNames[m.MirroredElemLabels]
    val fieldTypeclasses = getTypeclasses[m.MirroredElemTypes]

    value.productIterator.zip(fieldNames).zip(fieldTypeclasses).foldLeft(JsonObject(Map.empty)) {
      case (acc, ((item, fieldName), typeclass)) =>
        Json.semigroup.combine(
          acc,
          JsonObject(
            Map(fieldName -> typeclass.toJson(item))
          )
        )
    }
  }

  given stringEncoder: Encoder[String] = (value: String) => JsonString(value)

  given booleanEncoder: Encoder[Boolean] = (value: Boolean) => JsonBool(value)

  given longEncoder: Encoder[Long] = (value: Long) => JsonNum(value)

  given optionEncoder[A](using
    encoder: Encoder[A]
  ): Encoder[Option[A]] = {
    case Some(x) => encoder.toJson(x)
    case None    => JsonNull
  }

  given listEncoder[A](using
    encoder: Encoder[A]
  ): Encoder[List[A]] = (value: List[A]) => JsonArray(value.map(encoder.toJson))

  private inline def getFieldNames[A <: Tuple]: List[String] = inline erasedValue[A] match {
    case _: EmptyTuple => Nil
    case _: (head *: tail) =>
      val headFieldName: String        = constValue[head].toString
      val tailFieldNames: List[String] = getFieldNames[tail]
      headFieldName :: tailFieldNames
  }

  private inline def getTypeclasses[A <: Tuple]: List[Encoder[Any]] = inline erasedValue[A] match {
    case _: EmptyTuple => Nil
    case _: (head *: tail) =>
      val headTypeclass: Encoder[head] = summonInline[Encoder[head]]
      headTypeclass.asInstanceOf[Encoder[Any]] :: getTypeclasses[tail]
  }

}
