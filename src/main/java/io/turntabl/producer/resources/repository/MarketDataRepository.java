package io.turntabl.producer.resources.repository;

import io.turntabl.producer.resources.model.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

}
