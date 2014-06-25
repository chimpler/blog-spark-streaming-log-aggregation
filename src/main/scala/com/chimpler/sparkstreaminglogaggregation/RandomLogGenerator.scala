package com.chimpler.sparkstreaminglogaggregation

import java.util.Properties
import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}
import kafka.producer
import org.joda.time.DateTime

import scala.collection.JavaConversions._

import kafka.Kafka

import scala.util.Random

/**
 * Publish random logs to Kafka
 */
object RandomLogGenerator extends App {
  val NumPublishers = 5
  val NumAdvertisers = 3

  val publishers = (0 to NumPublishers).map("publisher_" +)
  val advertisers = (0 to NumAdvertisers).map("advertiser_" +)
  val geos = Seq("NY", "CA", "FL", "MI", "HI")

  val random = new Random()

  val props = new Properties()
  props ++= Map(
    "serializer.class" -> "kafka.serializer.StringEncoder",
    "metadata.broker.list" -> "127.0.0.1:9093"
  )

  val config = new ProducerConfig(props)
  val producer = new Producer[String, String](config)

  println("Sending messages...")
  var i = 0
  // infinite loop
  while(true) {
    val timestamp = DateTime.now
    val publisher = publishers(random.nextInt(NumPublishers))
    val advertiser = advertisers(random.nextInt(NumAdvertisers))
    val website = s"website_${random.nextInt(100)}.com"
    val cookie = s"cookie_${random.nextInt(100)}"
    val geo = if (random.nextInt(10) == 0) None else Some(geos(random.nextInt(geos.size)))
    val bid = math.abs(random.nextDouble()) % 1
    val log = Seq(timestamp, publisher, advertiser, website, geo.mkString, bid, cookie).mkString(",")
    producer.send(new KeyedMessage[String, String]("adnetwork-topic", log))
    i = i + 1
    if (i % 10000 == 0) {
      println(s"Sent $i messages!")
    }
  }
}
