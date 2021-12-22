package ezTick.stream;

import com.google.gson.Gson;
import ezTick.AppConfigs;
import ezTick.model.*;
import ezTick.serdes.AppSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamTopology {
    private static final Logger logger = LogManager.getLogger(Stream.class);
    private static final Gson serdes = new Gson();

    static public void withBuilder(StreamsBuilder streamsBuilder) {
        KTable<String, Tick> ticks = streamsBuilder.table(AppConfigs.tickTopicName, Consumed.with(AppSerdes.String(), AppSerdes.Tick()));
        KTable<String, Order> orders = streamsBuilder.table(AppConfigs.orderTopicName, Consumed.with(AppSerdes.String(), AppSerdes.Order()));

        KTable<String, Position> Positions = orders.leftJoin(ticks, Order::getSymbol,(order, tick) -> new Position(tick, order));
        Positions.toStream().to(AppConfigs.positionTopicName, Produced.with(Serdes.String(), AppSerdes.Position()));

        KTable<String, Position> PositionsTable = streamsBuilder.table(AppConfigs.positionTopicName, Consumed.with(AppSerdes.String(), AppSerdes.Position()));

        KTable<String, PortfolioAggregate> portfolioAggregates = PositionsTable.groupBy((k, v) -> KeyValue.pair(v.getPortfolio(), v), Grouped.with(AppSerdes.String(), AppSerdes.Position()))
                .aggregate(
                        //Initializer
                        () -> new PortfolioAggregate(),
                        //Adder
                        (k, v, aggV) -> {
                            float aggMktVal = 0;
                            float mktval = 0;
                            if (aggV != null && aggV.mktVal != null) {
                                aggMktVal = aggV.mktVal;
                            }
                            if (v != null && v.mktVal != null) {
                                mktval = v.mktVal;
                            }

                            PortfolioAggregate pa = new PortfolioAggregate();
                            pa.portfolio = k;

                            return pa.SetMktVal(aggMktVal + mktval);
                        },
                        //Subtractor
                        (k, v, aggV) -> {
                            float aggMktVal = 0;
                            float mktval = 0;
                            if (aggV != null && aggV.mktVal != null) {
                                aggMktVal = aggV.mktVal;
                            }
                            if (v != null && v.mktVal != null) {
                                mktval = v.mktVal;
                            }

                            PortfolioAggregate pa = new PortfolioAggregate();
                            pa.portfolio = k;

                            return pa.SetMktVal(aggMktVal - mktval);
                        },
                        //Serializer
                        Materialized.<String, PortfolioAggregate, KeyValueStore<Bytes, byte[]>>as(
                                AppConfigs.stateStoreName).withValueSerde(AppSerdes.PortfolioAggregate())
                );

        portfolioAggregates.toStream().to(AppConfigs.portfolioAggregatesTopicName, Produced.with(Serdes.String(), AppSerdes.PortfolioAggregate()));

        KTable<String, PortfolioAggregate> portfolioAggregatesTable = streamsBuilder.table(AppConfigs.portfolioAggregatesTopicName, Consumed.with(AppSerdes.String(), AppSerdes.PortfolioAggregate()));

        KTable<String, FinalViewQuery> finalViewQueryKTable = PositionsTable.leftJoin(portfolioAggregatesTable, Position::getPortfolio, (position, portfolioAggregate) -> new FinalViewQuery(position, portfolioAggregate));

        finalViewQueryKTable.toStream().to(AppConfigs.finalViewTopicName, Produced.with(Serdes.String(), AppSerdes.FinalViewQuery()));

        ticks.toStream().foreach((k, v) -> {
                    logger.info("Tick: " + "Key=" + k + " Value=" + serdes.toJson(v));
                }
        );

        orders.toStream().foreach((k, v) -> {
                    logger.info("Order: " + "Key=" + k + " Value=" + serdes.toJson(v));
                }
        );

        PositionsTable.toStream().foreach((k, v) -> {
            logger.info( "Join stream: "+ "Key=" + k + " Value=" + serdes.toJson(v));
        });

        portfolioAggregatesTable.toStream().foreach((k, v) -> {
            logger.info( "Aggregate stream: "+ "Key=" + k + " Value=" + serdes.toJson(v));
        });

        finalViewQueryKTable.toStream().foreach((k, v) -> {
            logger.info( "finalViewQuery stream: "+ "Key=" + k + " Value=" + serdes.toJson(v));
        });
    };

}
