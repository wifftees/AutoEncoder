package codecs

trait JsonReader[A] {
  def read(json: Json): Either[ReaderError, A]
}

object JsonReader {
  // Summoner function
  def apply[A]: JsonReader[A] = ???

  implicit class JsonReaderOps(val json: Json) extends AnyVal {
    def as[A]: Either[ReaderError, A] = ???
  }
}
