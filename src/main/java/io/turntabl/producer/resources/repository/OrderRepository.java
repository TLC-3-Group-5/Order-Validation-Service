package io.turntabl.producer.resources.repository;

import io.turntabl.producer.resources.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders,Long> {
}
