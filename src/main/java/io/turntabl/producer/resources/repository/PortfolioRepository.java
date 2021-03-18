package io.turntabl.producer.resources.repository;

import io.turntabl.producer.resources.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
