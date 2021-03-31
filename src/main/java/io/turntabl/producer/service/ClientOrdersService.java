package io.turntabl.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;
import io.turntabl.producer.resources.model.*;
import io.turntabl.producer.resources.service.MarketDataService;
import io.turntabl.producer.resources.service.OrderService;
import io.turntabl.producer.resources.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientOrdersService {

    private final MarketDataService marketDataService;

    @Autowired
    private final PortfolioService portfolioService;

    @Autowired
    private final OrderService orderService;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    @Value("${app.MARKET_DATA_EXCHANGE_1}")
    private String exchangeOneMarketData;

    @Value("${app.MARKET_DATA_EXCHANGE_2}")
    private String exchangeTwoMarketData;

    private int buyLimit;

    private int sellLimit;

    private double leastBidPrice;

    private double maxBidPrice;

    private double leastAskPrice;

    private double maxAskPrice;

    @Autowired
    public ClientOrdersService(MarketDataService marketDataService, PortfolioService portfolioService,
            OrderService orderService) {
        this.marketDataService = marketDataService;
        this.portfolioService = portfolioService;
        this.orderService = orderService;
    }

    public OrderResponse checkOrderValidity(OrderRequest request) {
        OrderResponse response = new OrderResponse();

        Double clientBalance = restTemplate.getForObject(env.getProperty("app.client_connectivity_service_url")
                .concat("/portfolio/client-balance/").concat(String.valueOf((request.getPortfolioId()))), Double.class);

        double balance = clientBalance != null ? clientBalance : 0;

        OwnedStockList stockList = restTemplate.getForObject(env.getProperty("app.client_connectivity_service_url")
                .concat("/portfolio/client-stocks/").concat(String.valueOf((request.getPortfolioId()))),
                OwnedStockList.class);

        MarketData marketData = marketDataService.getMarketDataByTicker(request.getProduct());

        ExchangeMarketData marketData_1 = null;
        try {
            marketData_1 = objectMapper
                    .readValue(restTemplate.getForObject(exchangeOneMarketData.concat(request.getProduct()), String.class),
                            ExchangeMarketData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ExchangeMarketData marketData_2 = null;
//        try {
//            marketData_2 = objectMapper
//                    .readValue(restTemplate.getForObject(exchangeTwoMarketData.concat(request.getProduct()), String.class),
//                            ExchangeMarketData.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        if(marketData_1!=null){
            buyLimit = marketData_1.getBUY_LIMIT();
            sellLimit = marketData_1.getSELL_LIMIT();
            double maxShiftPrice = marketData_1.getMAX_PRICE_SHIFT();
            double bidPrice = marketData_1.getBID_PRICE();
            double askPrice = marketData_1.getASK_PRICE();
            leastBidPrice = bidPrice - maxShiftPrice;
            maxBidPrice = bidPrice + maxShiftPrice;
            leastAskPrice = askPrice - maxShiftPrice;
            maxAskPrice = askPrice + maxShiftPrice;
        }


        // TODO Check BidPrice in the validation
        if (request.getSide().equals("BUY")) {
            if (balance != 0 && (request.getPrice() * request.getQuantity()) <= balance) {
                if (marketData_1 != null && marketData_1.getASK_PRICE()!=0) {
                    if (buyLimit > 0) {
                        if (request.getQuantity() <= buyLimit) {
                            if(request.getPrice()>=leastAskPrice && request.getPrice()<=maxAskPrice){
                                Orders orders = new Orders();
                                orders.setStatus("OPEN");
                                orders.setSide(request.getSide());
                                orders.setProduct(request.getProduct());
                                orders.setCreatedAt(LocalDateTime.now());
                                orders.setPrice(request.getPrice());
                                orders.setQuantity(request.getQuantity());
                                orders.setPortfolio(portfolioService.getPortfolio((long) request.getPortfolioId()));
                                orderService.createOrders(orders);

                                Double valueOfOrder = request.getQuantity() * request.getPrice();
                                Map<String, Long> variables = new HashMap<>();
                                variables.put("portfolioId", (long) request.getPortfolioId());

                                restTemplate.put(
                                        Optional.ofNullable(env.getProperty("app.client_connectivity_service_url"))
                                                .orElse("").concat("/portfolio/update-balance/{portfolioId}"),
                                        valueOfOrder, variables);

                                try {
                                    Jedis client = new Jedis(env.getProperty("app.SPRING_REDIS_URI"));

                                    client.publish("orders", objectMapper.writeValueAsString(orders));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                response.setIsOrderValid(true);
                                response.setMessage("Client order is valid");
                            }else{
                                response.setMessage("The price is not reasonable");
                            }
                        } else {
                            response.setIsOrderValid(false);
                            response.setMessage("Your requested quantity is greater than the buy limit");
                        }
                    } else {
                        response.setIsOrderValid(false);
                        response.setMessage("Product is unavailable no limit");
                    }
                } else {
                    response.setIsOrderValid(false);
                    response.setMessage("Product is unavailable");
                }
            } else {
                response.setIsOrderValid(false);
                response.setMessage("Client balance insufficient for the order");
            }
        }

        if (request.getSide().equals("SELL")) {
            if(stockList!=null){
                OwnedStock stock = stockList.getOwnedStockList().stream()
                        .filter(ownedStock -> ownedStock.getTicker().equals(request.getProduct())).findFirst().orElse(null);
                if (stock != null) {
                    if (sellLimit > 0) {
                        if (request.getQuantity() <= sellLimit && request.getQuantity()<= stock.getQuantity()) {
                            if(request.getPrice()>=leastBidPrice && request.getPrice()<=maxBidPrice){
                                // TODO Push order to Trade Engine via Content Pub/Sub
                                Orders orders = new Orders();
                                orders.setStatus("OPEN");
                                orders.setSide(request.getSide());
                                orders.setProduct(request.getProduct());
                                orders.setCreatedAt(LocalDateTime.now());
                                orders.setPrice(request.getPrice());
                                orders.setQuantity(request.getQuantity());
                                orders.setPortfolio(portfolioService.getPortfolio((long) request.getPortfolioId()));
                                orderService.createOrders(orders);

                                Map<String, Object> variables = new HashMap<>();
                                variables.put("portfolioId", (long) request.getPortfolioId());
                                variables.put("product", request.getProduct());

                                restTemplate.put(
                                        Optional.ofNullable(env.getProperty("app.client_connectivity_service_url")).orElse("")
                                                .concat("/portfolio/update-stock/{portfolioId}/{product}"),
                                        request.getQuantity(), variables);

                                try {
                                    Jedis client = new Jedis(
                                            Optional.ofNullable(env.getProperty("app.SPRING_REDIS_URI")).orElse(""));
                                    client.publish("orders", objectMapper.writeValueAsString(orders));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                response.setIsOrderValid(true);
                                response.setMessage("Client order is valid");
                            }

                        } else {
                            response.setIsOrderValid(false);
                            response.setMessage("You cannot sell more than " + request.getQuantity());
                        }
                    } else {
                        response.setIsOrderValid(false);
                        response.setMessage("You cannot sell here");
                    }
                } else {
                    response.setIsOrderValid(false);
                    response.setMessage("You don't have this product on your portfolio");
                }
            }else{
                response.setIsOrderValid(false);
                response.setMessage("You don't have this product on your portfolio");
            }
        }

        return response;
    }

}
