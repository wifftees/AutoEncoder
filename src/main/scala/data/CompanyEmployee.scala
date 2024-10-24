package data

import unmarshal.decoder.Decoder
import unmarshal.decoder.Decoder.retrieveField
import unmarshal.encoder.Encoder
import unmarshal.error.DecoderError
import unmarshal.error.DecoderError.wrongJson
import unmarshal.model.Json.{JsonArray, JsonObject}

case class CompanyEmployee(
  employees: List[Employee]
)

object CompanyEmployee {

  private val EMPLOYEES_FIELD = "employees"

  given companyEmployeeEncoder: Encoder[CompanyEmployee] = (companyEmployee: CompanyEmployee) =>
    JsonObject(
      Map(
        EMPLOYEES_FIELD -> JsonArray(companyEmployee.employees.map(Encoder[Employee].toJson(_)))
      )
    )

  given companyEmployeeDecoder: Decoder[CompanyEmployee] = {
    case JsonObject(map) if map.keys.toList.diff(List(EMPLOYEES_FIELD)).nonEmpty =>
      Left(wrongJson("Unknown field", map.keys.toList.diff(List(EMPLOYEES_FIELD)).head))
    case JsonObject(map) =>
      (for {
        employeesJson <- retrieveField(EMPLOYEES_FIELD, map)
      } yield for {
        employees <- employeesJson match {
          case JsonArray(list) => Right(list)
          case _               => Left(wrongJson("Wrong field type", EMPLOYEES_FIELD))
        }
      } yield employees
        .map(Decoder[Employee].fromJson(_))
        .zipWithIndex
        .map((x, index) =>
          x match {
            case Left(DecoderError(reason, field)) =>
              Left(wrongJson(reason, s"$EMPLOYEES_FIELD.$index.$field"))
            case x => x
          }
        )
        .foldLeft[Either[DecoderError, List[Employee]]](Right(List.empty[Employee]))((acc, x) =>
          for {
            currentAccList  <- acc
            currentEmployee <- x
          } yield currentAccList :+ currentEmployee
        )
        .flatMap(list => Right(CompanyEmployee(list)))).flatten.flatten

    case _ => Left(wrongJson("Given value is not json object", ""))
  }

}
