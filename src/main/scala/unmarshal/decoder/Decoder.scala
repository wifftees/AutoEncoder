package unmarshal.decoder

import unmarshal.error.DecoderError
import unmarshal.error.DecoderError.wrongJson
import unmarshal.model.Json

trait Decoder[A] {
  def fromJson(json: Json): Either[DecoderError, A]
}

object Decoder {

  def retrieveField(fieldName: String, map: Map[String, Json]): Either[DecoderError, Json] =
    map.get(fieldName) match {
      case Some(value) => Right(value)
      case None        => Left(wrongJson("Field not exists", fieldName))
    }

  implicit def apply[A](implicit
    decoder: Decoder[A]
  ): Decoder[A] = decoder

}
