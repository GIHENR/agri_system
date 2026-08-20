package com.agri.agri.controller;

import com.agri.agri.entity.User;
import com.agri.agri.repository.UserRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    public GlobalControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Automatically injects the "loggedInUser" object into EVERY HTML page
    @ModelAttribute("loggedInUser")
    public User addUserToModel(Principal principal) {
        if (principal != null) {
            // Find the user by their Spring Security username
            return userRepository.findByUsername(principal.getName()).orElse(null);
        }
        return null;
    }
}