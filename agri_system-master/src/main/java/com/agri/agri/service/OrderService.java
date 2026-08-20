package com.agri.agri.service;

import com.agri.agri.entity.CropBatch;
import com.agri.agri.entity.FarmOrder;
import com.agri.agri.entity.User;
import com.agri.agri.exception.InsufficientStockException;
import com.agri.agri.repository.CropBatchRepository;
import com.agri.agri.repository.FarmOrderRepository;
import com.agri.agri.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class OrderService {

    private final FarmOrderRepository farmOrderRepository;
    private final CropBatchRepository cropBatchRepository;
    private final UserRepository userRepository;

    public OrderService(FarmOrderRepository farmOrderRepository, CropBatchRepository cropBatchRepository, UserRepository userRepository) {
        this.farmOrderRepository = farmOrderRepository;
        this.cropBatchRepository = cropBatchRepository;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void processNewOrder(String username, String cropName, Integer quantity, BigDecimal totalCost) {

        //Fetch Crop
        CropBatch crop = cropBatchRepository.findByCropName(cropName)
                .orElseThrow(() -> new RuntimeException("Crop not found in database"));

        if (crop.getStockLevel() < quantity) {
            throw new InsufficientStockException("Not enough stock available.");
        }

        crop.setStockLevel(crop.getStockLevel() - quantity);
        cropBatchRepository.save(crop);

        //Fetch User to build the proper Entity Relationship
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Create Order
        FarmOrder order = new FarmOrder();
        order.setUser(buyer); // <-- This fixes the error!
        order.setItemsSummary(quantity + "x " + cropName);
        order.setTotalCost(totalCost);
        order.setDateReceived(LocalDate.now());
        order.setStatus("Pending");
        order.setOrderId("#ORD-" + (1000 + new Random().nextInt(9000)));

        farmOrderRepository.save(order);
    }
}