package com.chimpler.sparkstreaminglogaggregation

case class ImpressionLog(timestamp: Long, publisher: String, advertiser: String, website: String, geo: String, bid: Double, cookie: String)