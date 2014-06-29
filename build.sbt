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
    // for serialization of case class
    "com.novus" %% "salat" % "1.9.8",
    // MongoDB
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    // Joda dates for Scala
    "com.github.nscala-time" %% "scala-time" % "1.2.0"
  )
}

packSettings

packMain := Map(
  "publisher" -> "com.chimpler.sparkstreaminglogaggregation.RandomLogGenerator",
  "aggregator" -> "com.chimpler.sparkstreaminglogaggregation.Aggregator"
)