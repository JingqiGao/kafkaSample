package ezTick;

public class AppConfigs {
    public final static String ProducerApplicationID = "Producer";
    public final static String StreamApplicationID = "StreamEz";
    public final static String ConsumerID = "Consumer";
    public final static String stateStoreLocation = "tmp/state-store";
    public final static String stateStoreName = "kt00-store";
    public final static String bootstrapServers = "localhost:9092,localhost:9093";
    public final static String orderTopicName = "orders";
    public final static String tickTopicName = "ticks";
    public final static String positionTopicName = "positions";
    public final static String portfolioAggregatesTopicName = "portfolioAggregates";
    public final static String finalViewTopicName = "finalViewTopicName";
    public final static String groupID1 = "finalViewGroup1";
    public final static String groupID2 = "finalViewGroup2";


    public final static String[] eventFiles = {"data/NASDAQ.csv","data/NYSE.csv"};

    public final static String producerRestServerHost = "localhost";
    public final static int producerRestServerPort = 7020;

    public final static String streamRestServerHost = "localhost";
    public final static int streamRestServerPort = 7010;
}
