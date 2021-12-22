package ezTick.model;

public class Tick {
    public String symbol;
    public Float marketPrice;
    public String getSymbol() {
        return symbol;
    }

    public Tick() {
        symbol = "";
        marketPrice = 0f;
    }

    public Tick(String symbol, Float marketPrice) {
        this.symbol = symbol;
        this.marketPrice = marketPrice;
    }
}
