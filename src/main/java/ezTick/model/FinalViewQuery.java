package ezTick.model;

public class FinalViewQuery extends Position{
    public Float portfolioMktVal;
    public Float symbolMktVal;
    public Float exposure;
    FinalViewQuery() {
        super();
        this.portfolioMktVal = 0f;
        this.symbolMktVal = 0f;
        this.exposure = 0f;
    }

    public FinalViewQuery(Position position, PortfolioAggregate portfolioAggregate) {
        this.symbol = position.symbol;
        this.portfolio = position.portfolio;
        this.orderId = position.orderId;
        if (position.qty != null) {
            this.qty = position.qty;
        } else {
            this.qty = 0f;
        }
        if (position.tradePrice != null) {
            this.tradePrice = position.tradePrice;
        } else {
            this.tradePrice = 0f;
        }
        this.portfolio = position.portfolio;
        if (position.mktVal != null) {
            this.mktVal = position.mktVal;
        } else {
            this.mktVal = 0f;
        }
        if (position.tickPrice != null) {
            this.tickPrice = position.tickPrice;
        } else {
            this.tickPrice = 0f;
        }
        if (portfolioAggregate != null && portfolioAggregate.mktVal != 0 ) {
            this.portfolioMktVal = portfolioAggregate.mktVal;
            this.exposure = this.mktVal/this.portfolioMktVal;
        } else {
            this.portfolioMktVal = 0f;
            this.exposure = 0f;
        }
        this.symbolMktVal = 0f;
    }


}
