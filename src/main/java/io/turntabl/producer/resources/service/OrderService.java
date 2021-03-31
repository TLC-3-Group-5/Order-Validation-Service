package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.Orders;
import io.turntabl.producer.resources.model.Trade;
import io.turntabl.producer.resources.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Value("${app.client_connectivity_service_url}")
    private String clientUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    // Create Order
    public void createOrders(Orders orders){
        this.orderRepository.save(orders);
    }

    // Get Order
    public List<Trade> getOrderTrades(String id){
        Orders order = orderRepository.findById(Long.valueOf(id)).orElse(null);
        return order != null ?
                order.getTradeList()
                        .stream()
                        .filter(s->s.getStatus().equals("OPEN")).collect(Collectors.toList())
                : null;
    }

    // Update Order Status
    public void updateOrderStatus(Long id, String status){
        Orders order = orderRepository.findById(id).orElse(null);
        if(order!=null){
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    // Update Client Balance of Cancelled Order
    public void updateBalanceOfCancelledOrder(String orderId){
        Orders order = orderRepository.findById(Long.valueOf(orderId)).orElse(null);

        if(order != null){
            if(order.getStatus().equals("OPEN")){
                Map<String, Long> variables = new HashMap<>();
                variables.put("portfolioId", order.getPortfolio().getId());
                restTemplate.put(
                        clientUrl.concat("/portfolio/update-balance/{portfolioId}"),
                        (order.getPrice()*order.getQuantity()), variables);
            }
        }
    }
}
