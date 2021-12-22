package ezTick.stream;

import com.google.gson.Gson;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;
import ezTick.AppConfigs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.ArrayList;
import java.util.List;

class StreamRestService {
    private static final Logger logger = LogManager.getLogger();
    private final String NO_RESULTS = "{\"code\": 404}";
    private final String APPLICATION_NOT_ACTIVE = "Application is not active. Try later.";
    private final KafkaStreams streams;
    private Boolean isActive = false;
    private final HostInfo hostInfo;
    private Client client;
    private Gson serdes;


    StreamRestService(KafkaStreams streams, String hostname, int port) {
        this.streams = streams;
        this.hostInfo = new HostInfo(hostname, port);
        client = ClientBuilder.newClient();
        serdes = new Gson();

    }

    void setActive(Boolean state) {
        isActive = state;
    }

    private List<KeyValue<String, String>> readAllFromLocal() {

        List<KeyValue<String, String>> localResults = new ArrayList<>();
        StoreQueryParameters<ReadOnlyKeyValueStore<String, String>> sQP = StoreQueryParameters.fromNameAndType(AppConfigs.stateStoreName, QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<String, String> stateStore =
                streams.store(sQP);
        stateStore.all().forEachRemaining(localResults::add);
        return localResults;
    }

    void start() {
        logger.info("Starting Query Server at http://" + hostInfo.host() + ":" + hostInfo.port()
                + "/" + AppConfigs.stateStoreName + "/all");

        Spark.port(hostInfo.port());

        Spark.get("/" + AppConfigs.stateStoreName + "/all", (req, res) -> {

            List<KeyValue<String, String>> allResults;
            String results;

            allResults = readAllFromLocal();
            results = (allResults.size() == 0) ? NO_RESULTS
                    : serdes.toJson(allResults);

            return results;
        });

    }

    void stop() {
        client.close();
        Spark.stop();
    }

}
