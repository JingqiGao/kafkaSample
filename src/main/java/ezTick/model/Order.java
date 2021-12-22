package ezTick.model;

public class Order {
    public int orderId;
    public String symbol;
    public Float qty;
    public Float tradePrice;
    public String portfolio;

    public Order() {
        orderId = 0;
        symbol = "";
        qty = 0f;
        tradePrice = 0f;
        portfolio = "";
    }

    public int getOrderId() {
        return orderId;
    }
    public String getSymbol() {
        return symbol;
    }

}
