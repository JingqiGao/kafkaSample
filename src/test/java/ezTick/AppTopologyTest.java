package ezTick;

import com.google.gson.Gson;
import ezTick.model.Order;

import ezTick.model.PortfolioAggregate;
import ezTick.model.Position;
import ezTick.model.Tick;
import ezTick.serdes.AppSerdes;
import ezTick.stream.StreamTopology;
import org.apache.kafka.streams.*;

import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppTopologyTest {
    private TopologyTestDriver topologyTestDriver;
    private TestInputTopic<String, String> orderTopic;
    private TestInputTopic<String, String> tickTopic;
    private TestOutputTopic<String, Position> positionTopic;
    private Gson serdes;


    @BeforeEach
    public void setup() {
        serdes = new Gson();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.StreamApplicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        StreamTopology.withBuilder(streamsBuilder);
        Topology topology = streamsBuilder.build();

        topologyTestDriver = new TopologyTestDriver(topology, props);
        orderTopic = topologyTestDriver.createInputTopic(AppConfigs.orderTopicName,  AppSerdes.String().serializer(), AppSerdes.String().serializer());
        tickTopic = topologyTestDriver.createInputTopic(AppConfigs.tickTopicName, AppSerdes.String().serializer(), AppSerdes.String().serializer());
        positionTopic = topologyTestDriver.createOutputTopic(AppConfigs.positionTopicName, AppSerdes.String().deserializer(), AppSerdes.Position().deserializer());
    }

    @AfterEach
    public void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    public void positionFlowTest() {
        Tick tick = new Tick();
        Order order = new Order();
        tick.symbol = "AAPL";
        tick.marketPrice = 100f;
        order.symbol = "AAPL";
        order.portfolio = "Portfolio A";
        order.orderId = 1;
        order.tradePrice = 100f;
        order.qty = 200f;

        orderTopic.pipeInput(Integer.toString(order.orderId),
                serdes.toJson(order));
        positionTopic.readKeyValue();

        tickTopic.pipeInput(tick.symbol, serdes.toJson(tick));

        KeyValue<String, Position> record = positionTopic.readKeyValue();

        KeyValueStore<String, PortfolioAggregate> portfolioAggregate = topologyTestDriver.getKeyValueStore(AppConfigs.stateStoreName);

        assertAll(
                () -> assertEquals("AAPL", record.value.symbol),
                () -> assertEquals(20000f, record.value.mktVal),
                () -> assertEquals("1", record.key),
                () -> assertEquals("Portfolio A", portfolioAggregate.get("Portfolio A").portfolio),
                () -> assertEquals(20000f, portfolioAggregate.get("Portfolio A").mktVal)
        );
    }

}