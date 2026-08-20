package com.agri.agri.service;

import com.agri.agri.entity.FarmOrder;
import com.agri.agri.repository.FarmOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderHistoryService {

    private final FarmOrderRepository farmOrderRepository;

    public OrderHistoryService(FarmOrderRepository farmOrderRepository) {
        this.farmOrderRepository = farmOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<FarmOrder> getBuyerHistory(String username) {
        return farmOrderRepository.findByUser_UsernameOrderByIdDesc(username);
    }
}