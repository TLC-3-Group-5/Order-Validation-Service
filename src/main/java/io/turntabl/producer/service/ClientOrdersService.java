package io.turntabl.producer.service;

import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;

import io.turntabl.producer.resources.model.MarketData;
import io.turntabl.producer.resources.model.OwnedStock;
import io.turntabl.producer.resources.model.OwnedStockList;
import io.turntabl.producer.resources.service.MarketDataService;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientOrdersService {

    private final MarketDataService marketDataService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    Environment env;

    @Autowired
    public ClientOrdersService(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public OrderResponse checkOrderValidity(OrderRequest request){
        OrderResponse response = new OrderResponse();

        Double clientBalance = restTemplate.getForObject(env.getProperty("clientservice")
            .concat("/portfolio/client-balance/").concat(String.valueOf((request.getPortfolioId()))), Double.class);

        double balance = clientBalance != null ? clientBalance: 0;

        OwnedStockList stockList = restTemplate.getForObject(env.getProperty("clientservice")
                .concat("/portfolio/client-stocks/").concat(String.valueOf((request.getPortfolioId()))), OwnedStockList.class);

        MarketData marketData = marketDataService.getMarketDataByTicker(request.getProduct());

        //TODO Check BidPrice in the validation
        if(request.getSide().equals("BUY")) {
            if(balance!= 0 && (request.getPrice() * request.getQuantity()) <=balance){
                if(marketData.getBuyLimit()>0){
                    if(request.getQuantity()<marketData.getBuyLimit()){
                        //TODO Push order to Trade Engine via Content Pub/Sub
                        response.setIsOrderValid(true);
                        response.setMessage("Client order is valid");
                    }else{
                        response.setIsOrderValid(false);
                        response.setMessage("Your requested quantity is greater than the buy limit");
                    }
                }else{
                    response.setIsOrderValid(false);
                    response.setMessage("Product is unavailable");
                }
            }else{
                response.setIsOrderValid(false);
                response.setMessage("Client balance insufficient for the order");
            }
        }

        if(request.getSide().equals("SELL")){
            assert stockList != null;
            OwnedStock stock = stockList.getOwnedStockList().stream()
                    .filter(ownedStock->ownedStock.getTicker().equals(request.getProduct()))
                    .findFirst().orElse(null);
            if(stock!=null){
                if(marketData.getSellLimit()>0){
                    if(request.getQuantity()<marketData.getSellLimit()){

                        //TODO Push order to Trade Engine via Content Pub/Sub
                        response.setIsOrderValid(true);
                        response.setMessage("Client order is valid");
                    }else{
                        response.setIsOrderValid(false);
                        response.setMessage("You cannot sell more than " + request.getQuantity() );
                    }
                }else{
                    response.setIsOrderValid(false);
                    response.setMessage("You cannot sell here");
                }
            }else{
                response.setIsOrderValid(false);
                response.setMessage("You don't have this product on your portfolio");
            }
        }

        return response;
    }

}
