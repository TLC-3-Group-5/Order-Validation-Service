package io.turntabl.producer.resources.model;

import java.util.List;

public class TradeList {
    private List<Trade> tradeList;

    public TradeList() {
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<Trade> tradeList) {
        this.tradeList = tradeList;
    }
}
