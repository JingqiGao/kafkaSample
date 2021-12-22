package ezTick.consumer;
import com.google.gson.Gson;
import ezTick.AppConfigs;
import ezTick.model.FinalViewQuery;
import ezTick.serdes.GsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Consumer2 {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson serdes = new Gson();

    public static void main(String[] args) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, AppConfigs.ConsumerID);
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class);
        consumerProps.put(GsonDeserializer.CONFIG_VALUE_CLASS, FinalViewQuery.class);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfigs.groupID2);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, FinalViewQuery> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList(AppConfigs.finalViewTopicName));

        while (true) {
            ConsumerRecords<String, FinalViewQuery> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, FinalViewQuery> record : records) {
                {
                    logger.info("valid record - " + serdes.toJson(record.value()));
                }
            }
        }
    }
}
