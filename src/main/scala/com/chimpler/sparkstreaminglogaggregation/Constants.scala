package com.chimpler.sparkstreaminglogaggregation

object Constants {
  val NumPublishers = 5
  val NumAdvertisers = 3

  val Publishers = (0 to NumPublishers).map("publisher_" +)
  val Advertisers = (0 to NumAdvertisers).map("advertiser_" +)
  val Geos = Seq("NY", "CA", "FL", "MI", "HI", "unknown")
  val NumWebsites = 10000
  val NumCookies = 100000

  val KafkaTopic = "adnetwork-topic"
}