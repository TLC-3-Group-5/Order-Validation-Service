package io.turntabl.producer.resources.repository;

import io.turntabl.producer.resources.model.OwnedStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedStockRepository extends JpaRepository<OwnedStock,Long> {
}
