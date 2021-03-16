package io.turntabl.producer.resources.controller;

import io.turntabl.producer.resources.model.MarketData;
import io.turntabl.producer.resources.service.MarketDataService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class MarketDataProviderController {
  
  private static Logger logger = LoggerFactory.getLogger(MarketDataProviderController.class);
  private final MarketDataService marketDataService;

  @Autowired
  MarketDataProviderController(MarketDataService mds) {
    marketDataService = mds;
  }

  @PostMapping("/market-data")
  public void onMarketDataUpdate(@RequestBody List<MarketData> md) {
    logger.info(md.toString());

    // * clear the table data
    marketDataService.clearAllData();

    // * recreate the table with new data
    marketDataService.appendData(md);
  }
}
