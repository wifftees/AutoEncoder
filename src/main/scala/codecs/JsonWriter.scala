package codecs

trait JsonWriter[A] {
  def write(a: A): Json
}

object JsonWriter {
  // Summoner function
  def apply[A]: JsonWriter[A] = ???

  implicit class JsonWriterOps[A](val a: A) {
    def toJson: Json = ???
  }
}
