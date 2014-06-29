package com.chimpler.sparkstreaminglogaggregation


import com.novus.salat
import com.novus.salat.global._
import kafka.serializer.{Decoder, Encoder}
import kafka.utils.VerifiableProperties
import org.apache.commons.io.Charsets

// encode and decode logs in JSON (in this tuto for readability purpose) but it would be better to consider something like AVRO or protobuf)
class ImpressionLogDecoder(props: VerifiableProperties) extends Decoder[ImpressionLog] {
  def fromBytes(bytes: Array[Byte]): ImpressionLog = {
    salat.grater[ImpressionLog].fromJSON(new String(bytes, Charsets.UTF_8))
  }
}

class ImpressionLogEncoder(props: VerifiableProperties) extends Encoder[ImpressionLog] {
  def toBytes(impressionLog: ImpressionLog): Array[Byte] = {
    salat.grater[ImpressionLog].toCompactJSON(impressionLog).getBytes(Charsets.UTF_8)
  }
}