package ezTick.producer;

import ezTick.AppConfigs;
import ezTick.model.Order;
import ezTick.model.Tick;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class Producer {
    private static final Logger logger = LogManager.getLogger(Producer.class);

    public static void main(String[] args) {
        String hostName = AppConfigs.producerRestServerHost;
        int portNumber = AppConfigs.producerRestServerPort;
        logger.info("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, AppConfigs.ProducerApplicationID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.ACKS_CONFIG,"all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        ProducerRestService queryServer = new ProducerRestService(producer, hostName, portNumber);
        queryServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping producer");
            queryServer.stop();
            producer.close();
        }));
    }
}
