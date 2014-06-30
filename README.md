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
    2014-06-29T04:47:46.953Z,"publisher_2","FL",1081,1079,0.4957418622666391
    2014-06-29T04:47:46.954Z,"publisher_0","MI",1038,1026,0.5035403308281741
    2014-06-29T04:47:46.954Z,"publisher_2","MI",973,975,0.5017696377463182
    2014-06-29T04:47:46.956Z,"publisher_2","NY",1132,1134,0.5049172113219206
    2014-06-29T04:47:46.954Z,"publisher_3","MI",1091,1074,0.5133811885761826
    2014-06-29T04:47:46.954Z,"publisher_1","FL",1050,1070,0.5009583813806119
    2014-06-29T04:47:46.954Z,"publisher_0","NY",1067,1067,0.5085223237227676