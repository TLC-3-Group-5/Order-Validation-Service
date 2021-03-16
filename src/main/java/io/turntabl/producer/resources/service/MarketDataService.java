package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.MarketData;
import io.turntabl.producer.resources.repository.MarketDataRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketDataService {
  
  private final MarketDataRepository marketDataRepository;

  @Autowired
  MarketDataService(MarketDataRepository mdr) {
    marketDataRepository = mdr;
  }

  public void clearAllData() {
    marketDataRepository.deleteAll();
  }

  public List<MarketData> appendData(List<MarketData> data) {
    return marketDataRepository.saveAll(data);
  }

  public MarketData getMarketDataByTicker(String product){
    return marketDataRepository.findByTicker(product).orElse(null);
  }
}
