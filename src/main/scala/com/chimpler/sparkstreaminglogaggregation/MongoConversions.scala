package com.chimpler.sparkstreaminglogaggregation

import org.joda.time.DateTime
import reactivemongo.bson._

object MongoConversions {
  implicit object DateTimeHandler extends BSONHandler[BSONDateTime, DateTime] {
    def read(t: BSONDateTime) = new DateTime(t.value)

    def write(t: DateTime) = BSONDateTime(t.getMillis)
  }
}