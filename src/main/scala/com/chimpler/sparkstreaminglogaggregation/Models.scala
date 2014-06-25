package com.chimpler.sparkstreaminglogaggregation

import org.joda.time.DateTime

case class ImpressionLog(timestamp: DateTime, publisher: String, advertiser: String, website: String, geo: Option[String], bid: Double, cookie: String)