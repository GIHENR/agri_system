package com.agri.agri.repository;

import com.agri.agri.entity.FarmOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmOrderRepository extends JpaRepository<FarmOrder, Long> {

    // Count orders by their status
    long countByStatus(String status);

    // Get the most recent orders for the dashboard
    List<FarmOrder> findTop5ByOrderByDateReceivedDesc();

    List<FarmOrder> findByUser_UsernameOrderByIdDesc(String username);
}