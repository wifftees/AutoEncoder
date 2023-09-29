package codecs

sealed abstract class ReaderError(message: String, field: String)
case class WrongType(field: String, message: String = "Wrong field type") extends ReaderError(message, field)
case class AbsentField(field: String, message: String = "Absent field") extends ReaderError(message, field)
