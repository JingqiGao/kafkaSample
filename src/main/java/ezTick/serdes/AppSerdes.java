package ezTick.serdes;

import ezTick.model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

public class AppSerdes extends Serdes {

    static final class OrderSerde extends WrapperSerde<Order> {
        OrderSerde() { super(new GsonSerializer<>(), new GsonDeserializer<>());}
    }

    public static Serde<Order> Order() {
        OrderSerde serde = new OrderSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(GsonDeserializer.CONFIG_VALUE_CLASS, Order.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class TickSerde extends WrapperSerde<Tick> {
        TickSerde() { super(new GsonSerializer<>(), new GsonDeserializer<>());}
    }

    public static Serde<Tick> Tick() {
        TickSerde serde = new TickSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(GsonDeserializer.CONFIG_VALUE_CLASS, Tick.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class PositionSerde extends WrapperSerde<Position> {
        PositionSerde() { super(new GsonSerializer<>(), new GsonDeserializer<>());}
    }

    public static Serde<Position> Position() {
        PositionSerde serde = new PositionSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(GsonDeserializer.CONFIG_VALUE_CLASS, Position.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class PortfolioAggregateSerde extends WrapperSerde<PortfolioAggregate> {
        PortfolioAggregateSerde() { super(new GsonSerializer<>(), new GsonDeserializer<>());}
    }

    public static Serde<PortfolioAggregate> PortfolioAggregate() {
        PortfolioAggregateSerde serde = new PortfolioAggregateSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(GsonDeserializer.CONFIG_VALUE_CLASS, PortfolioAggregate.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class FinalViewQuerySerde extends WrapperSerde<FinalViewQuery> {
        FinalViewQuerySerde() { super(new GsonSerializer<>(), new GsonDeserializer<>());}
    }

    public static Serde<FinalViewQuery> FinalViewQuery() {
        FinalViewQuerySerde serde = new FinalViewQuerySerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(GsonDeserializer.CONFIG_VALUE_CLASS, FinalViewQuery.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

}
