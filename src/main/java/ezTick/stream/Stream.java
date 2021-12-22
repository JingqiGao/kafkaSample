package ezTick.stream;

import com.google.gson.Gson;
import ezTick.AppConfigs;
import ezTick.model.*;
import ezTick.producer.Producer;
import ezTick.serdes.AppSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class Stream {
    private static final Logger logger = LogManager.getLogger(Stream.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.StreamApplicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        StreamTopology.withBuilder(streamsBuilder);

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);

        StreamRestService queryServer = new StreamRestService(streams, AppConfigs.streamRestServerHost, AppConfigs.streamRestServerPort);
        streams.setStateListener((newState, oldState) -> {
            logger.info("State Changing to " + newState + " from " + oldState);
            queryServer.setActive(newState == KafkaStreams.State.RUNNING && oldState == KafkaStreams.State.REBALANCING);
        });

        logger.info("Starting stream.");
        streams.cleanUp();
        streams.start();
        queryServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down stream");
            streams.close();
            streams.cleanUp();
            queryServer.stop();
        }));
    }
}
