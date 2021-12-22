package ezTick.model;

public class PortfolioAggregate {
    public String portfolio;
    public Float mktVal;

    public Float getMktVal() {
        return mktVal;
    }

    public PortfolioAggregate() {
        this.portfolio = "";
        this.mktVal = 0f;
    }

    public PortfolioAggregate(Position position) {
        this.portfolio = position.portfolio;
        this.mktVal = position.mktVal;
    }

    public PortfolioAggregate SetMktVal(Float mktval) {
        this.mktVal = mktval;
        return this;
    }

//    public PortfolioAggregate SetQty(Float qty) {
//        this.qty = qty;
//        return this;
//    }
}
