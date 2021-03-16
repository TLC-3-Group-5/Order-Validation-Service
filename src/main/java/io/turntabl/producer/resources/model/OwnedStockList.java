package io.turntabl.producer.resources.model;

import java.util.List;

public class OwnedStockList {
    private List<OwnedStock> ownedStockList;

    public OwnedStockList() {
    }

    public List<OwnedStock> getOwnedStockList() {
        return ownedStockList;
    }

    public void setOwnedStockList(List<OwnedStock> ownedStockList) {
        this.ownedStockList = ownedStockList;
    }
}
