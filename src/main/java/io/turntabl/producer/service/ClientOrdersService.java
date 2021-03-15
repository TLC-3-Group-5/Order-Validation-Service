package io.turntabl.producer.service;

import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public class ClientOrdersService {

    public OrderResponse checkOrderValidity(OrderRequest request){
        OrderResponse response = new OrderResponse();

        if(request.getSide().equals("BUY")){
            if((request.getPrice()* request.getQuantity())>=200){
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
