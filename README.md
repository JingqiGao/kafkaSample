# kafkaSample
Command to start kafka and create topics
```
zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
kafka-server-start.sh $KAFKA_HOME/config/server.properties
kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic ticks --bootstrap-server localhost:9092
kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic orders --bootstrap-server localhost:9092
kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic positions --bootstrap-server localhost:9092
kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic portfolioAggregates --bootstrap-server localhost:9092
kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic finalViewTopicName --bootstrap-server localhost:9092 
```

- Consumer - Consume broadcast with different groupIds
- Producer - Use Gson serdes to create producer for any data type
- Stream - Join with foreign key, group and aggregate
- Interactive query to query dataStore
- Unit test
