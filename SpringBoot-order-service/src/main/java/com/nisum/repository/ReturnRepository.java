package com.nisum.repository;


import com.nisum.entity.ReturnCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<ReturnCase, Long> {
    List<ReturnCase> findByOrderId(String orderId);
    List<ReturnCase> findByCustomerId(String customerId);
}
