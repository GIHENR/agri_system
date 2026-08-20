package com.agri.agri.controller;

import com.agri.agri.entity.FarmOrder;
import com.agri.agri.repository.CropBatchRepository;
import com.agri.agri.repository.FarmOrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class OrderController {

    private final FarmOrderRepository farmOrderRepository;
    private final CropBatchRepository cropBatchRepository;

    public OrderController(FarmOrderRepository farmOrderRepository, CropBatchRepository cropBatchRepository) {
        this.farmOrderRepository = farmOrderRepository;
        this.cropBatchRepository = cropBatchRepository;
    }

    @GetMapping("/order")
    public String showOrderPage(Model model, Principal principal) {

        model.addAttribute("username", principal != null ? principal.getName() : "User");
        model.addAttribute("availableCrops", cropBatchRepository.findAll());

        return "order.u";
    }

    @GetMapping("/order_m")
    public String showOrderManagementPage(Model model) {
        // Farmer view to manage all incoming orders
        model.addAttribute("orders", farmOrderRepository.findAll());
        return "order_m";
    }

    @GetMapping("/order_history")
    public String showOrderHistoryPage(Model model, Principal principal) {
        // Spring Security secures this page for Buyers
        model.addAttribute("username", principal != null ? principal.getName() : "User");
        return "order_history.u";
    }

    // Handle Status Updates
    @PostMapping("/order/update-status")
    public String updateOrderStatus(@RequestParam Long id, @RequestParam String status) {
        Optional<FarmOrder> orderOptional = farmOrderRepository.findById(id);
        if (orderOptional.isPresent()) {
            FarmOrder order = orderOptional.get();
            order.setStatus(status);
            farmOrderRepository.save(order);
        }
        return "redirect:/order_m";
    }

    // Handle Deletions
    @PostMapping("/order/delete")
    public String deleteOrder(@RequestParam Long id) {
        farmOrderRepository.deleteById(id);
        return "redirect:/order_m";
    }
}