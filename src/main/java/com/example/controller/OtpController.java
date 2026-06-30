package com.example.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.entity.AppUsersEntity;
import com.example.repository.AppUsersRepository;
import com.example.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OtpController {

    @Autowired
    private AppUsersRepository userRepository;

    @Autowired
    private EmailService emailService;

    /* ================= SEND OTP ================= */
    @PostMapping("/send-otp")
    @ResponseBody
    public String sendOtp(@RequestParam String username) {

        AppUsersEntity user = userRepository.findByUsername(username);

        if (user == null) return "USER_NOT_FOUND";

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        Date expiry = new Date(System.currentTimeMillis() + 1 * 60 * 1000);

        userRepository.updateOtp(username, otp, expiry);
        
        // 🔥 SEND EMAIL
        emailService.sendOtp(user.getEmail().toString(), otp);

        System.out.println("OTP: " + otp);
        System.out.println("Sending OTP to: " + user.getEmail());

        return "OTP_SENT";
    }

    /* ================= VERIFY OTP ================= */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String username,
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        AppUsersEntity user = userRepository.findByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "index";
        }

        if (user.getOtp() == null || !otp.equals(user.getOtp())) {
            model.addAttribute("error", "Invalid OTP");
            return "index";
        }

        Date now = new Date();

        if (user.getOtpExpiry() == null || user.getOtpExpiry().before(now)) {
            model.addAttribute("error", "OTP expired");
            return "index";
        }

        // SUCCESS
        session.setAttribute("username", username);

        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return "redirect:/dashboard";
    }
    
    @GetMapping("/test-mail")
	@ResponseBody
	public String testMail() {
	    emailService.sendOtp("cchinna120@gmail.com", "123456");
	    emailService.sendOtp("eswarisai4371@gmail.com", "123456");
	    return "sent";
	}
}