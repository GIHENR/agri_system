package com.agri.agri.controller;

import com.agri.agri.entity.User;
import com.agri.agri.repository.ReportLogRepository;
import com.agri.agri.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ReportLogRepository reportLogRepository;

    // Injecting both repositories via the constructor
    public AdminController(UserRepository userRepository, ReportLogRepository reportLogRepository) {
        this.userRepository = userRepository;
        this.reportLogRepository = reportLogRepository;
    }

    @GetMapping("/farmer_management")
    public String farmerManagement(Model model) {
        List<User> farmers = userRepository.findByRoleAndIsVerified("FARMER", true);
        model.addAttribute("farmers", farmers);
        return "Farmer_Management_a";
    }

    @GetMapping("/buyer_management")
    public String buyerManagement(Model model) {
        List<User> buyers = userRepository.findByRoleAndIsVerified("BUYER", true);
        model.addAttribute("buyers", buyers);
        return "Buyer_Management_a";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        // Fetches the latest 20 reports from the database and passes them to the HTML
        model.addAttribute("recentReports", reportLogRepository.findTop20ByOrderByGeneratedAtDesc());
        return "Report_Generation";
    }


    @PostMapping("/admin/user/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String role,
                          @RequestParam(defaultValue = "/farmer_management") String redirectUrl) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role.toUpperCase());
        user.setPassword("default123");
        user.setVerified(true);

        userRepository.save(user);
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/admin/user/edit")
    public String editUser(@RequestParam Long id,
                           @RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String role,
                           @RequestParam(defaultValue = "/farmer_management") String redirectUrl) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role.toUpperCase());
            userRepository.save(user);
        }
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(@RequestParam Long id,
                             @RequestParam(defaultValue = "/farmer_management") String redirectUrl) {
        userRepository.deleteById(id);
        return "redirect:" + redirectUrl;
    }
}