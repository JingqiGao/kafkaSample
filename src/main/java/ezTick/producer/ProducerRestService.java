package ezTick.producer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ezTick.AppConfigs;
import ezTick.model.Tick;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.state.*;
import ezTick.model.Order;
import spark.Spark;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ProducerRestService {
    private static final Logger logger = LogManager.getLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaProducer<String, String>  producer;
    private final HostInfo hostInfo;
    private Client client;
    private Boolean isActive = false;
    private final String NOT_FOUND = "{\"code\": 204, \"message\": \"No Content\"}";
    private final String SERVICE_UNAVAILABLE = "{\"code\": 503, \"message\": \"Service Unavailable\"}";
    private final String OK = "{\"code\": 200}";
    private final String NO_RESULTS = "{\"code\": 404}";
    private Gson serdes;


    private Set<String> symbolSet = new HashSet<>();

    ProducerRestService(KafkaProducer<String, String>  producer, String hostname, int port) {
        this.producer = producer;
        this.hostInfo = new HostInfo(hostname, port);
        client = ClientBuilder.newClient();
        serdes = new Gson();

    }

    void start() {
        logger.info("Starting Producer " + "http://" + hostInfo.host() + ":" + hostInfo.port());
        Spark.port(hostInfo.port());
        Gson serdes = new Gson();

        Spark.post("/order", (req, res) -> {
            try {
                Type listOfMyClassObject = new TypeToken<ArrayList<Order>>() {}.getType();
                ArrayList<Order> orders = serdes.fromJson(req.body(), listOfMyClassObject);
                for (Order order : orders) {
                    RecordMetadata metadata;
                    metadata = producer.send(new ProducerRecord<>(AppConfigs.orderTopicName, Integer.toString(order.getOrderId()), serdes.toJson(order))).get();
                    logger.info("Message " + order.getOrderId() + " persisted with offset " + metadata.offset()
                            + " and timestamp on " + new Timestamp(metadata.timestamp()));
                }
                return OK;
            } catch (Exception e) {
                logger.info("Can't send message - Received exception \n" + e.getMessage());
                return OK;
            }
        });

        Spark.post("/tick", (req, res) -> {
            try {
                Type listOfMyClassObject = new TypeToken<ArrayList<Tick>>() {}.getType();
                ArrayList<Tick> ticks = serdes.fromJson(req.body(), listOfMyClassObject);
                for (Tick tick : ticks) {
                    RecordMetadata metadata;
                    metadata = producer.send(new ProducerRecord<>(AppConfigs.tickTopicName, tick.getSymbol(), serdes.toJson(tick))).get();
                    logger.info("Message " + tick.getSymbol() + " persisted with offset " + metadata.offset()
                            + " and timestamp on " + new Timestamp(metadata.timestamp()));
                }
                return OK;
            } catch (Exception e) {
                logger.info("Can't send message - Received exception \n" + e.getMessage());
                return OK;
            }
        });

        Spark.delete("/order", (req, res) -> {
            try {
                Type listOfMyClassObject = new TypeToken<ArrayList<Order>>() {}.getType();
                ArrayList<Order> orders = serdes.fromJson(req.body(), listOfMyClassObject);
                for (Order order : orders) {
                    RecordMetadata metadata;
                    metadata = producer.send(new ProducerRecord<>(AppConfigs.orderTopicName, Integer.toString(order.getOrderId()), null)).get();
                    logger.info("Message " + order.getOrderId() + " persisted with offset " + metadata.offset()
                            + " and timestamp on " + new Timestamp(metadata.timestamp()));
                }
                return OK;
            } catch (Exception e) {
                logger.info("Can't send message - Received exception \n" + e.getMessage());
                return OK;
            }
        });

        Spark.get("/health", (req, res) -> {
            return OK;
        });
    }



    void stop() {
        client.close();
        Spark.stop();
    }
}
