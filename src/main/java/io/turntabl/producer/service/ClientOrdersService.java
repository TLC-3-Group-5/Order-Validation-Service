package io.turntabl.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;

import io.turntabl.producer.resources.model.MarketData;
import io.turntabl.producer.resources.model.Orders;
import io.turntabl.producer.resources.model.OwnedStock;
import io.turntabl.producer.resources.model.OwnedStockList;
import io.turntabl.producer.resources.service.MarketDataService;
import io.turntabl.producer.resources.service.OrderService;
import io.turntabl.producer.resources.service.PortfolioService;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

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

        System.out.println(marketData);
        // TODO Check BidPrice in the validation
        if (request.getSide().equals("BUY")) {
            if (balance != 0 && (request.getPrice() * request.getQuantity()) <= balance) {
                if (marketData != null) {
                    if (marketData.getBuyLimit() > 0) {
                        if (request.getQuantity() < marketData.getBuyLimit()) {
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
            assert stockList != null;
            OwnedStock stock = stockList.getOwnedStockList().stream()
                    .filter(ownedStock -> ownedStock.getTicker().equals(request.getProduct())).findFirst().orElse(null);
            if (stock != null) {
                if (marketData.getSellLimit() > 0) {
                    if (request.getQuantity() < marketData.getSellLimit()) {

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
        }

        return response;
    }

}
