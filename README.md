Simple example consuming an adserver logs stream from Kafka.

More information on our blog: http://chimpler.wordpress.com/2014/06/29/implementing-a-real-time-data-pipeline-with-spark-streaming

In order to run our example, we need to install the followings:

* [Scala 2.10+](http://www.scala-lang.org/)
* [SBT](http://www.scala-sbt.org/)
* [Apache Zookeeper](http://zookeeper.apache.org/)
* [Apache Kafka](http://kafka.apache.org/)
* [MongoDB](http://www.mongodb.org/)


Building the examples:
    
    $ sbt pack

Create a topic “adnetwork-topic”:
    
    $ kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic adnetwork-topic
    
Start Zookeeper:
   
    $ zookeeper-server-start.sh config/zookeeper.properties
    
Start Kafka:

    $ kafka-server-start.sh config/kafka-server1.properties
    
On one window, run the aggregator:

    $ target/pack/aggregator

On the other one, run the adserver log random generator:

    $ target/pack/generator
You can also see the messages that are sent using the Kafka console consumer:

    $ kafka-console-consumer.sh --topic adnetwork-topic --zookeeper localhost:2181
    
After a few seconds, you should see the results in MongoDB:

    $ mongoexport -d adlogdb -c impsPerPubGeo --csv -f date,publisher,geo,imps,uniques,avgBids
    connected to: 127.0.0.1
     
    date,publisher,geo,imps,uniques,avgBids
    2014-07-01T03:24:39.679Z,"publisher_4","CA",3980,3248,0.50062253292876
    2014-07-01T03:24:39.681Z,"publisher_4","MI",3958,3229,0.505213545705667
    2014-07-01T03:24:39.681Z,"publisher_1","HI",3886,3218,0.4984981221446526
    2014-07-01T03:24:39.681Z,"publisher_3","CA",3937,3226,0.5038157362872939
    2014-07-01T03:24:39.679Z,"publisher_4","NY",3894,3200,0.5022389599376207
    2014-07-01T03:24:39.679Z,"publisher_2","HI",3906,3240,0.4988378174961185
    2014-07-01T03:24:39.679Z,"publisher_3","HI",3989,3309,0.4975347625823641
    2014-07-01T03:24:39.681Z,"publisher_3","FL",3957,3167,0.4993339490605483