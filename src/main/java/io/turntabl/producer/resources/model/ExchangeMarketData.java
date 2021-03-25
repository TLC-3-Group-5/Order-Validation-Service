package io.turntabl.producer.resources.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeMarketData {

    @JsonProperty("LAST_TRADED_PRICE")
    private Double LAST_TRADED_PRICE;

    @JsonProperty("BID_PRICE")
    private Double BID_PRICE;

    @JsonProperty("SELL_LIMIT")
    private int SELL_LIMIT;

    @JsonProperty("MAX_PRICE_SHIFT")
    private Double MAX_PRICE_SHIFT;

    @JsonProperty("TICKER")
    private String TICKER;

    @JsonProperty("ASK_PRICE")
    private Double ASK_PRICE;

    @JsonProperty("BUY_LIMIT")
    private int BUY_LIMIT;

    public ExchangeMarketData() {
    }

    public Double getLAST_TRADED_PRICE() {
        return LAST_TRADED_PRICE;
    }

    public void setLAST_TRADED_PRICE(Double LAST_TRADED_PRICE) {
        this.LAST_TRADED_PRICE = LAST_TRADED_PRICE;
    }

    public Double getBID_PRICE() {
        return BID_PRICE;
    }

    public void setBID_PRICE(Double BID_PRICE) {
        this.BID_PRICE = BID_PRICE;
    }

    public int getSELL_LIMIT() {
        return SELL_LIMIT;
    }

    public void setSELL_LIMIT(int SELL_LIMIT) {
        this.SELL_LIMIT = SELL_LIMIT;
    }

    public Double getMAX_PRICE_SHIFT() {
        return MAX_PRICE_SHIFT;
    }

    public void setMAX_PRICE_SHIFT(Double MAX_PRICE_SHIFT) {
        this.MAX_PRICE_SHIFT = MAX_PRICE_SHIFT;
    }

    public String getTICKER() {
        return TICKER;
    }

    public void setTICKER(String TICKER) {
        this.TICKER = TICKER;
    }

    public Double getASK_PRICE() {
        return ASK_PRICE;
    }

    public void setASK_PRICE(Double ASK_PRICE) {
        this.ASK_PRICE = ASK_PRICE;
    }

    public int getBUY_LIMIT() {
        return BUY_LIMIT;
    }

    public void setBUY_LIMIT(int BUY_LIMIT) {
        this.BUY_LIMIT = BUY_LIMIT;
    }

    @Override
    public String toString() {
        return "ExchangeMarketData{" +
                "LAST_TRADED_PRICE=" + LAST_TRADED_PRICE +
                ", BID_PRICE=" + BID_PRICE +
                ", SELL_LIMIT=" + SELL_LIMIT +
                ", MAX_PRICE_SHIFT=" + MAX_PRICE_SHIFT +
                ", TICKER='" + TICKER + '\'' +
                ", ASK_PRICE=" + ASK_PRICE +
                ", BUY_LIMIT=" + BUY_LIMIT +
                '}';
    }
}
