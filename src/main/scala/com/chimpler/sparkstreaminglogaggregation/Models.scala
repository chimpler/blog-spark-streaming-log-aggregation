package com.chimpler.sparkstreaminglogaggregation

import com.twitter.algebird.HLL
import org.joda.time.DateTime

case class ImpressionLog(timestamp: Long, publisher: String, advertiser: String, website: String, geo: String, bid: Double, cookie: String)

// intermediate result used in reducer
case class AggregationLog(timestamp: Long, sumBids: Double, imps: Int = 1, uniquesHll: HLL)

// result to be stored in MongoDB
case class AggregationResult(date: DateTime, publisher: String, geo: String, imps: Int, uniques: Int, avgBids: Double)

case class PublisherGeoKey(publisher: String, geo: String)

