package com.agri.agri.controller;

import com.agri.agri.dto.OrderRequest;
import com.agri.agri.entity.FarmOrder;
import com.agri.agri.exception.InsufficientStockException;
import com.agri.agri.service.OrderHistoryService;
import com.agri.agri.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;

    public OrderApiController(OrderService orderService, OrderHistoryService orderHistoryService) {
        this.orderService = orderService;
        this.orderHistoryService = orderHistoryService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            orderService.processNewOrder(
                    principal.getName(),
                    payload.getCropName(),
                    payload.getQuantity(),
                    payload.getTotalCost()
            );

            return ResponseEntity.ok().body("Order placed successfully");

        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed.");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        List<FarmOrder> history = orderHistoryService.getBuyerHistory(principal.getName());
        return ResponseEntity.ok(history);
    }
}