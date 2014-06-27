package com.chimpler.sparkstreaminglogaggregation

import com.twitter.algebird.HyperLogLogMonoid
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.commons.io.Charsets
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object LogAggregator extends App {

  val sparkContext = new SparkContext("local[4]", "logAggregator")
  val streamingContext = new StreamingContext(sparkContext, Seconds(1))

  val kafkaParams = Map(
    "zookeeper.connect" -> "localhost:2181",
    "zookeeper.connection.timeout.ms" -> "10000",
    "group.id" -> "myGroup"
  )

  val topics = Map(
    "adnetwork-topic" -> 4
  )

  val messages = KafkaUtils.createStream[String, ImpressionLog, StringDecoder, ImpressionLogDecoder](streamingContext, kafkaParams, topics, StorageLevel.MEMORY_AND_DISK)
  // messages is an RDD[(topic, ImpressionLog)]
  messages.map(_._2).foreachRDD(aggregateLogs(_))
  streamingContext.start()

  def aggregateLogs(logRdd: RDD[ImpressionLog]) {

    // group by geo and compute count, uniques and average bid
    logRdd.groupBy(l => (l.publisher, l.geo)).foreach {
      case ((publisher, geo), logs) =>
        val imps = logs.size

        val hll = new HyperLogLogMonoid(12)
        val estimatedUniques = logs.map(log => hll(log.cookie.getBytes(Charsets.UTF_8))).reduce(_ + _).estimatedSize

        val averageBid = logs.map(_.bid).reduce(_ + _) / imps

        val aggregationLog = AggregationLog(0L, publisher, geo, imps, estimatedUniques.toInt, averageBid)
        println("==> " + aggregationLog)
    }
  }
}