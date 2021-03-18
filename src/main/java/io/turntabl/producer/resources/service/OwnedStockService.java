package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.repository.OwnedStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnedStockService {

    private final OwnedStockRepository ownedStockRepository;

    @Autowired
    public OwnedStockService(OwnedStockRepository ownedStockRepository){
        this.ownedStockRepository = ownedStockRepository;
    }
}
