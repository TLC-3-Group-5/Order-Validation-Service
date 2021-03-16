package io.turntabl.producer.resources.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table
@Entity(name = "MarketData")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketData {

  @Id
  @SequenceGenerator(name = "client_sequence", sequenceName = "client_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence")
  @Column(nullable = false, updatable = false)
  private Long id;

  @JsonProperty(value = "LAST_TRADED_PRICE")
  @Column(nullable = false)
  private double lastTradedPrice;
  
  @JsonProperty(value = "BID_PRICE")
  @Column(nullable = false)
  private double bidPrice;
  
  @JsonProperty(value = "SELL_LIMIT")
  @Column(nullable = false)
  private int sellLimit;
  
  @JsonProperty(value = "MAX_PRICE_SHIFT")
  @Column(nullable = false)
  private double maxPriceShift;
  
  @JsonProperty(value = "TICKER")
  @Column(nullable = false)
  private String ticker;
  
  @JsonProperty(value = "ASK_PRICE")
  @Column(nullable = false)
  private double askPrice;
  
  @JsonProperty(value = "BUY_LIMIT")
  @Column(nullable = false)
  private int buyLimit;

  public MarketData() {
  }

  public MarketData(final double lastTradedPrice, final double bidPrice, final int sellLimit,
      final double maxPriceShift, final String ticker, final double askPrice, final int buyLimit) {
    this.lastTradedPrice = lastTradedPrice;
    this.bidPrice = bidPrice;
    this.sellLimit = sellLimit;
    this.maxPriceShift = maxPriceShift;
    this.ticker = ticker;
    this.askPrice = askPrice;
    this.buyLimit = buyLimit;
  }

  public double getLastTradedPrice() {
    return lastTradedPrice;
  }

  public double getBidPrice() {
    return bidPrice;
  }

  public int getSellLimit() {
    return sellLimit;
  }

  public double getMaxPriceShift() {
    return maxPriceShift;
  }

  public String getTicker() {
    return ticker;
  }

  public double getAskPrice() {
    return askPrice;
  }

  public int getBuyLimit() {
    return buyLimit;
  }

  @Override
  public String toString() {
    return "MarketData {askPrice=" + askPrice + ", bidPrice=" + bidPrice + ", buyLimit=" + buyLimit
        + ", lastTradedPrice=" + lastTradedPrice + ", maxPriceShift=" + maxPriceShift + ", sellLimit=" + sellLimit
        + ", ticker=" + ticker + "}";
  }

  public void setLastTradedPrice(double lastTradedPrice) {
    this.lastTradedPrice = lastTradedPrice;
  }

  public void setBidPrice(double bidPrice) {
    this.bidPrice = bidPrice;
  }

  public void setSellLimit(int sellLimit) {
    this.sellLimit = sellLimit;
  }

  public void setMaxPriceShift(double maxPriceShift) {
    this.maxPriceShift = maxPriceShift;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public void setAskPrice(double askPrice) {
    this.askPrice = askPrice;
  }

  public void setBuyLimit(int buyLimit) {
    this.buyLimit = buyLimit;
  }
}
