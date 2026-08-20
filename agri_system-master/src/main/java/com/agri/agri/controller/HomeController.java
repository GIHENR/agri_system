package com.agri.agri.controller;

import com.agri.agri.repository.CropBatchRepository;
import com.agri.agri.repository.FarmOrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CropBatchRepository cropBatchRepository;
    private final FarmOrderRepository farmOrderRepository;

    public HomeController(CropBatchRepository cropBatchRepository, FarmOrderRepository farmOrderRepository) {
        this.cropBatchRepository = cropBatchRepository;
        this.farmOrderRepository = farmOrderRepository;
    }

    @GetMapping("/")
    public String showDashboard(Model model) {
        //Calculate Stats
        Integer totalStock = cropBatchRepository.getTotalStock();
        long pendingOrders = farmOrderRepository.countByStatus("Pending");
        long recentActivity = farmOrderRepository.count() + cropBatchRepository.count(); // Simple combined metric

        //Send to Frontend
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("recentActivity", recentActivity);

        //Send recent transactions
        model.addAttribute("recentTransactions", farmOrderRepository.findTop5ByOrderByDateReceivedDesc());

        return "index";
    }
}