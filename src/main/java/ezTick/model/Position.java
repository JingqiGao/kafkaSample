package ezTick.model;

public class Position {
    public int orderId;
    public String symbol;
    public Float qty;
    public Float tradePrice;
    public String portfolio;
    public Float mktVal;
    public Float tickPrice;

    public Position() {
        this.orderId = 0;
        this.qty = 0f;
        this.tradePrice = 0f;
        this.mktVal = 0f;
        this.tickPrice = 0f;
    }


    public Position(Tick tick, Order order) {
        this.orderId = order.orderId;
        this.symbol = order.symbol;
        this.qty = order.qty;
        this.tradePrice = order.tradePrice;
        this.portfolio = order.portfolio;
        if (tick != null) {
            this.mktVal = order.qty * tick.marketPrice;
            this.tickPrice = tick.marketPrice;
        } else {
            this.mktVal = 0f;
            this.tickPrice =0f;
        }
    }

    public String getPortfolio() {
        return portfolio;
    }
}
