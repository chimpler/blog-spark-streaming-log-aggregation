package com.chimpler.sparkstreaminglogaggregation

import com.github.nscala_time.time.Imports._
import com.twitter.algebird.HyperLogLogMonoid
import kafka.serializer.StringDecoder
import org.apache.commons.io.Charsets
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import MongoConversions._

import scala.concurrent.ExecutionContext.Implicits.global

object LogAggregator extends App {
  val BatchDuration = Seconds(10)

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))

  implicit val aggHandler = Macros.handler[AggregationResult]

  val db = connection("adlogdb")
  val collection = db[BSONCollection]("impsPerPubGeo")

  val sparkContext = new SparkContext("local[4]", "logAggregator")

  // we discretize the stream in BatchDuration seconds intervals
  val streamingContext = new StreamingContext(sparkContext, BatchDuration)

  val kafkaParams = Map(
    "zookeeper.connect" -> "localhost:2181",
    "zookeeper.connection.timeout.ms" -> "10000",
    "group.id" -> "myGroup"
  )

  val topics = Map(
    Constants.KafkaTopic -> 1
  )

  // stream of (topic, ImpressionLog)
  val messages = KafkaUtils.createStream[String, ImpressionLog, StringDecoder, ImpressionLogDecoder](streamingContext, kafkaParams, topics, StorageLevel.MEMORY_AND_DISK)

  // to count uniques
  lazy val hyperLogLog = new HyperLogLogMonoid(12)

  // we filter out non resolved geo (unknown) and map (pub, geo) -> AggLog that will be reduced
  val logsByPubGeo = messages.map(_._2).filter(_.geo != Constants.UnknownGeo).map {
    log =>
      val key = PublisherGeoKey(log.publisher, log.geo)
      val agg = AggregationLog(
        timestamp = log.timestamp,
        sumBids = log.bid,
        imps = 1,
        uniquesHll = hyperLogLog(log.cookie.getBytes(Charsets.UTF_8))
      )
      (key, agg)
  }

  // Reduce to generate imps, uniques, sumBid per pub and geo per interval of BatchDuration seconds
  import org.apache.spark.streaming.StreamingContext._
  val aggLogs = logsByPubGeo.reduceByKeyAndWindow(reduceAggregationLogs, BatchDuration)

  // Store in MongoDB
  aggLogs.foreachRDD(saveLogs(_))

  // start rolling!
  streamingContext.start()

  private def saveLogs(logRdd: RDD[(PublisherGeoKey, AggregationLog)]) {
    val logs = logRdd.map {
      case (PublisherGeoKey(pub, geo), AggregationLog(timestamp, sumBids, imps, uniquesHll)) =>
        AggregationResult(new DateTime(timestamp), pub, geo, imps, uniquesHll.estimatedSize.toInt, sumBids / imps)
    }.collect()

    // save in MongoDB
    logs.foreach(collection.save(_))
  }

  private def reduceAggregationLogs(aggLog1: AggregationLog, aggLog2: AggregationLog) = {
    aggLog1.copy(
      timestamp = math.min(aggLog1.timestamp, aggLog2.timestamp),
      sumBids = aggLog1.sumBids + aggLog2.sumBids,
      imps = aggLog1.imps + aggLog2.imps,
      uniquesHll = aggLog1.uniquesHll + aggLog2.uniquesHll
    )
  }
}