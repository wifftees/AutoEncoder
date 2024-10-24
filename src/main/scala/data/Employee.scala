package data

import unmarshal.decoder.Decoder
import unmarshal.decoder.Decoder.retrieveField
import unmarshal.error.DecoderError.wrongJson
import unmarshal.encoder.Encoder
import unmarshal.model.Json._

case class Employee(
  name: String,
  age: Long,
  id: Long,
  bossId: Option[Long]
)

object Employee {

  private val NAME_FIELD    = "name"
  private val AGE_FIELD     = "age"
  private val ID_FIELD      = "id"
  private val BOSS_ID_FIELD = "bossId"
  private val listOfFields  = List(NAME_FIELD, AGE_FIELD, ID_FIELD, BOSS_ID_FIELD)

  given employeeEncoder: Encoder[Employee] = (value: Employee) =>
    JsonObject(
      Map(
        NAME_FIELD -> JsonString(value.name),
        AGE_FIELD  -> JsonNum(value.age),
        ID_FIELD   -> JsonNum(value.id),
        BOSS_ID_FIELD -> (value.bossId match {
          case Some(id) => JsonNum(id)
          case None     => JsonNull
        })
      )
    )

  given employeeDecoder: Decoder[Employee] = {
    case JsonObject(map) if map.keys.toList.diff(listOfFields).nonEmpty =>
      Left(wrongJson("Unknown field", map.keys.toList.diff(listOfFields).head))
    case JsonObject(map) =>
      (for {
        nameJson   <- retrieveField(NAME_FIELD, map)
        ageJson    <- retrieveField(AGE_FIELD, map)
        idJson     <- retrieveField(ID_FIELD, map)
        bossIdJson <- retrieveField(BOSS_ID_FIELD, map)
      } yield for {
        name <- nameJson match {
          case JsonString(name) => Right(name)
          case _                => Left(wrongJson("Wrong field type", NAME_FIELD))
        }
        age <- ageJson match {
          case JsonNum(age) => Right(age)
          case _            => Left(wrongJson("Wrong field type", AGE_FIELD))
        }
        id <- idJson match {
          case JsonNum(id) => Right(id)
          case _           => Left(wrongJson("Wrong field type", ID_FIELD))
        }
        bossId <- bossIdJson match {
          case JsonNum(bossId) => Right(Some(bossId))
          case JsonNull        => Right(None)
          case _               => Left(wrongJson("Wrong field type", BOSS_ID_FIELD))
        }
      } yield Employee(name, age, id, bossId)).flatten
    case _ => Left(wrongJson("Given value is not json object", ""))
  }

}
