package com.nisum.tools;

/*
import com.nisum.client.ReturnServiceClient;
import com.nisum.dto.CreateReturnRequest;
import com.nisum.dto.ProductCategory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class ReturnAgentTools {

    private final ReturnServiceClient returnServiceClient;

    public ReturnAgentTools(ReturnServiceClient returnServiceClient) {
        this.returnServiceClient = returnServiceClient;
    }

    @Tool(
            name = "create_return_request",
            description = """
            Submit a return or refund request to the Return Service.
            Use this tool only after all required information has been
            collected and confirmed by the customer.
            Do not invent order details, prices, dates or product categories.
            """
    )
    public String createReturnRequest(

            @ToolParam(description = "Customer order ID")
            String orderId,

            @ToolParam(description = "Customer ID")
            String customerId,

            @ToolParam(description = "Product ID")
            String productId,

            @ToolParam(description = "Product name")
            String productName,

            @ToolParam(description = "Quantity to return")
            Integer quantity,

            @ToolParam(description = "Price per item")
            BigDecimal itemPrice,

            @ToolParam(description = "Reason: DAMAGED, EXPIRED, MISSING, WRONG_ITEM or CHANGED_MIND")
            String reason,

            @ToolParam(description = "Delivery date in yyyy-MM-dd format")
            String deliveryDate,

            @ToolParam(description = "Product category: GROCERY, ELECTRONICS, CLOTHING or OTHER")
            ProductCategory productCategory
    ) {

        LocalDate parsedDeliveryDate;

        try {
            parsedDeliveryDate = LocalDate.parse(deliveryDate);
        } catch (DateTimeParseException ex) {
            return "Invalid delivery date. Please provide the date in yyyy-MM-dd format.";
        }
        CreateReturnRequest request = new CreateReturnRequest(
                orderId,
                customerId,
                productId,
                productName,
                quantity,
                itemPrice,
                reason,
                parsedDeliveryDate,
                productCategory
        );

        return returnServiceClient.createReturn(request);
    }
}*/


import com.nisum.client.OrderApiClient;
import com.nisum.client.ReturnServiceClient;
import com.nisum.dto.CreateReturnRequest;
import com.nisum.dto.OrderResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ReturnAgentTools {

    private final OrderApiClient orderServiceClient;
    private final ReturnServiceClient returnServiceClient;

    public ReturnAgentTools(
            OrderApiClient orderServiceClient,
            ReturnServiceClient returnServiceClient) {

        this.orderServiceClient = orderServiceClient;
        this.returnServiceClient = returnServiceClient;
    }

    @Tool(description = "Fetch order details using the order ID")
    public OrderResponse getOrderDetails(Long orderId) {

        OrderResponse order =
                orderServiceClient.getByOrderId(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        return order;
    }

    @Tool(description = """
            Create a return request for an order.
            Provide the order ID and customer's return reason.
            Fetch the order details before creating the return.
            """)
    public String createReturnRequest(
            @ToolParam(description = "The order ID as a Long, for example 1L")
            Long orderId,

            @ToolParam(description = "The customer's return reason, for example DAMAGED")
            String reason) {

        // Step 1: Fetch order details
        OrderResponse order =
                orderServiceClient.getByOrderId(orderId);

        if (order == null) {
            return "Order not found for ID: " + orderId;
        }

        // Step 2: Validate order information
        if (order.deliveryDate() == null) {
            return "This order has not been delivered yet. "
                    + "A return cannot be created.";
        }

        if (!"CONFIRMED".equalsIgnoreCase(order.status())) {
            return "This order is not marked as delivered. "
                    + "Current status: " + order.status();
        }

        // Step 3: Build return request using order details
        CreateReturnRequest request = new CreateReturnRequest(
                String.valueOf(order.id()),
                order.customerId(),
                order.productId(),
                order.product(),
                order.quantity(),
                order.itemPrice(),
                reason,
                order.deliveryDate(),
                order.productCategory()
        );

        // Step 4: Call Return Service
        return returnServiceClient.createReturn(request);
    }
}

