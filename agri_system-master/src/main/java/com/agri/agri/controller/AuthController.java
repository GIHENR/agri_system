package com.agri.agri.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.agri.agri.entity.User;
import com.agri.agri.repository.UserRepository;
import com.agri.agri.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.Random;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JavaMailSender mailSender;

    // Constructor Injection
    public AuthController(UserRepository userRepository, EmailService emailService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.mailSender = mailSender;
    }

    // Displays the HTML page when you visit http://localhost:8080/login
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Handles the form registration submission
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role) {

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Note: If using NoOpPasswordEncoder from config, plain text works.
        newUser.setRole(role);

        String code = String.format("%06d", new Random().nextInt(999999));
        newUser.setVerificationCode(code);

        userRepository.save(newUser);
        emailService.sendVerificationEmail(email, code);

        return "redirect:/login?verify=true";
    }

    // Handles the email verification step
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String code) {
        Optional<User> userOptional = userRepository.findByVerificationCode(code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return "redirect:/login?success=true";
        }

        return "redirect:/login?error=invalid_code";
    }

    //Check Email and Send Reset Code
    @PostMapping("/request-reset")
    public String sendResetCode(@RequestParam String email, HttpSession session) {
        Optional<User> userOptional = userRepository.findFirstByEmail(email);

        if (userOptional.isEmpty()) {
            return "redirect:/login?error=email_not_found";
        }

        //Stop unverified accounts from resetting passwords
        if (!userOptional.get().isVerified()) {
            return "redirect:/login?error=not_verified";
        }

        // Generate a 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("resetCode", code);
        session.setAttribute("resetEmail", email);

        //send the email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Code");
            message.setText("Your password reset code is: " + code);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error=mail_failed";
        }

        return "redirect:/login?reset=true";
    }

    //Verify Code and Update Password
    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam String resetCode,
                                 @RequestParam String newPassword,
                                 HttpSession session) {

        String savedCode = (String) session.getAttribute("resetCode");
        String savedEmail = (String) session.getAttribute("resetEmail");

        if (savedCode != null && savedCode.equals(resetCode)) {

            User user = userRepository.findFirstByEmail(savedEmail).get();
            user.setPassword(newPassword);
            userRepository.save(user);


            session.removeAttribute("resetCode");
            session.removeAttribute("resetEmail");

            return "redirect:/login?success=password_updated";
        }

        return "redirect:/login?error=invalid_reset_code";
    }
}