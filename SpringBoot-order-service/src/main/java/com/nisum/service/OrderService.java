package com.nisum.service;

import com.nisum.dto.request.CreateOrderRequest;
import com.nisum.dto.response.OrderResponse;
import com.nisum.entity.OrderEntity;
import com.nisum.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // Create a new order
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        OrderEntity entity = new OrderEntity(
                request.customerId(),
                request.productId(),
                request.product(),
                request.quantity(),
                request.itemPrice(),
                request.productCategory(),
                request.deliveryDate()
        );

        OrderEntity savedEntity = repository.save(entity);

        return toResponse(savedEntity);
    }

    // Find order by ID
    @Transactional(readOnly = true)
    public Optional<OrderResponse> findById(Long id) {

        return repository.findById(id)
                .map(this::toResponse);
    }

    // Find orders by customer ID
    @Transactional(readOnly = true)
    public List<OrderResponse> findByCustomer(String customerId) {

        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Cancel an order
    @Transactional
    public Optional<OrderResponse> cancel(Long id) {

        return repository.findById(id).map(entity -> {

            if (!"CONFIRMED".equals(entity.getStatus())) {
                throw new IllegalStateException(
                        "Only confirmed orders can be cancelled"
                );
            }

            entity.setStatus("CANCELLED");

            return toResponse(repository.save(entity));
        });
    }

    // Convert Entity to Response DTO
    private OrderResponse toResponse(OrderEntity entity) {

        return new OrderResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProduct(),
                entity.getQuantity(),
                entity.getItemPrice(),
                entity.getProductCategory(),
                entity.getStatus(),
                entity.getDeliveryDate(),
                entity.getCreatedAt()
        );
    }
}