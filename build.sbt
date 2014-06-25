name := "blog-spark-streaming-log-aggregation"

organization := "com.chimpler"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion = "1.0.0"

libraryDependencies <<= scalaVersion {
  scala_version => Seq(
    // Spark and Spark Streaming
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
    // Kafka
    "org.apache.kafka" %% "kafka" % "0.8.1.1",
    // Algebird (used here for HyperLogLog)
    "com.twitter" %% "algebird-core" % "0.6.0",
    // Mongo
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    // Joda (date time)
    "joda-time" % "joda-time" % "2.3"
  )
}

packSettings

packMain := Map(
  "publisher" -> "com.chimpler.sparkstreaminglogaggregation.RandomLogGenerator",
  "aggregator" -> "com.chimpler.sparkstreaminglogaggregation.Aggregator"
)