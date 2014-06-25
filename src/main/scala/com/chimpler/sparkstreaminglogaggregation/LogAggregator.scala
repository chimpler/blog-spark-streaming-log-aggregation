package com.chimpler.sparkstreaminglogaggregation

import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object LogAggregator extends App {

  val sparkContext = new SparkContext("local[4]", "logAggregator")
  val streamingContext = new StreamingContext(sparkContext, Seconds(10))

  val kafkaParams = Map(
    "zookeeper.connect" -> "localhost:2181",
    "zookeeper.connection.timeout.ms" -> "10000",
    "group.id" -> "myGroup"
  )

  val topics = Map(
    "adnetwork-topic" -> 1
  )

  val messages = KafkaUtils.createStream[String, ImpressionLog, StringDecoder, DefaultDecoder](streamingContext, kafkaParams, topics, StorageLevel.MEMORY_AND_DISK)
  messages.foreachRDD(s => aggregateLogs(s))
  streamingContext.start()

  def aggregateLogs(rdd: RDD[(String, ImpressionLog)]) {
    println(rdd.collect().size)
  }
}
