package io.turntabl.producer.service;

import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientOrdersService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    Environment env;

    public OrderResponse checkOrderValidity(OrderRequest request){
        OrderResponse response = new OrderResponse();

        Double clientBalance = restTemplate.getForObject(env.getProperty("clientservice")
            .concat("/portfolio/client-balance/").concat(request.getPortfolioId()), Double.class);
        
        System.out.println("Client balance: " + clientBalance);

        if(request.getSide().equals("BUY")) {
            if((request.getPrice() * request.getQuantity()) >= 200){
                response.setIsOrderValid(true);
                response.setMessage("Client order is valid");
            }else{
                response.setIsOrderValid(false);
                response.setMessage("Client balance insufficient for the order");
            }
        }

        return response;
    }

}
