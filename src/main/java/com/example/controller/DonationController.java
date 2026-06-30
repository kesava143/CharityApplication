package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class DonationController {

	@GetMapping("/donations")
	public String donations(HttpServletRequest request, HttpSession session) {

		return "donations";
	}
}
