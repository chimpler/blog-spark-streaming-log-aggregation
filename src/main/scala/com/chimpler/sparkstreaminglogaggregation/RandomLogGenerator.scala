package com.chimpler.sparkstreaminglogaggregation

import java.util.{Date, Properties}
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
  val geos = Seq("NY", "CA", "FL", "MI", "HI", "unknown")

  val random = new Random()

  val props = new Properties()
  props ++= Map(
    "serializer.class" -> "com.chimpler.sparkstreaminglogaggregation.ImpressionLogEncoder",
    "metadata.broker.list" -> "127.0.0.1:9093"
  )

  val config = new ProducerConfig(props)
  val producer = new Producer[String, ImpressionLog](config)

  println("Sending messages...")
  var i = 0
  // infinite loop
  while(true) {
    val timestamp = System.currentTimeMillis()
    val publisher = publishers(random.nextInt(NumPublishers))
    val advertiser = advertisers(random.nextInt(NumAdvertisers))
    val website = s"website_${random.nextInt(100)}.com"
    val cookie = s"cookie_${random.nextInt(10000)}"
    val geo = geos(random.nextInt(geos.size))
    val bid = math.abs(random.nextDouble()) % 1
    val log = ImpressionLog(timestamp, publisher, advertiser, website, geo, bid, cookie)
    producer.send(new KeyedMessage[String, ImpressionLog]("adnetwork-topic", log))
    i = i + 1
    if (i % 10000 == 0) {
      println(s"Sent $i messages!")
    }
  }
}
