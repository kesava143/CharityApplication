package com.example.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.AppUsersEntity;
import com.example.entity.UserLoginAuditEntity;
import com.example.repository.AppUsersRepository;
import com.example.repository.UserLoginAuditRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private AppUsersRepository userRepository;

	@Autowired
	private UserLoginAuditRepository auditRepository;

	private String getClientIp(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// Convert IPv6 localhost to IPv4
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}

		return ip;
	}
	
	/* ================= LOGIN PAGE ================= */
	@GetMapping({ "/", "/login" })
	public String loginPage(HttpSession session) {

		// ✅ If already logged in → go dashboard
		if (session.getAttribute("username") != null) {
			return "redirect:/dashboard";
		}

		return "index";
	}

	/* ================= LOGIN SUBMIT ================= */
	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response,
			HttpServletRequest request, HttpSession session, Model model) {

		AppUsersEntity user = userRepository.login(username, password);

		if (user == null) {
			model.addAttribute("error", "Invalid username or password");
			return "index";
		}

		// 🔥 GET IP
		String ip = getClientIp(request);

		// ✅ Store in session
		session.setAttribute("username", user.getUsername());

		// 🔥 UPDATE LOGIN TIME
		userRepository.updateLoginTime(user.getUsername());

		// 🔥 INSERT AUDIT RECORD
		UserLoginAuditEntity audit = new UserLoginAuditEntity();
		audit.setUsername(user.getUsername());
		audit.setLoginTime(LocalDateTime.now());
		audit.setIpAddress(ip);

		audit = auditRepository.save(audit);

		// 🔥 STORE AUDIT ID IN SESSION
		session.setAttribute("auditId", audit.getId());

		return "redirect:/dashboard";
	}

	/* ================= MAIN DASHBOARD SHELL ================= */
	@GetMapping("/dashboard")
	public String dashboard(HttpServletRequest request, HttpSession session) {

		Object user = session.getAttribute("username");

		// 🔒 Final check
		if (user == null) {
			return "redirect:/login";
		}

		return "dashboard"; // Loads your dashboard.html shell
	}

	/* ================= IFRAME SUB-PAGES ================= */
	
	@GetMapping("/dashboard/overview")
	public String overview(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "overview"; // Loads templates/overview.html
	}

	@GetMapping("/dashboard/whatsapp")
	public String whatsapp(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "whatsapp"; // Loads templates/whatsapp.html
	}

	@GetMapping("/dashboard/users-list")
	public String usersList(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "users"; // Loads templates/users.html
	}

	@GetMapping("/dashboard/donations")
	public String donations(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "donations"; // Loads templates/donations.html
	}

	@GetMapping("/dashboard/reports")
	public String reports(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "reports"; // Loads templates/reports.html
	}

	@GetMapping("/dashboard/settings")
	public String settings(HttpSession session) {
		if (session.getAttribute("username") == null) return "redirect:/login";
		return "settings"; // Loads templates/settings.html
	}


	/* ================= LOGOUT ================= */
	@GetMapping("/logout")
	public String logout(HttpServletResponse response, HttpSession session) {

		String username = (String) session.getAttribute("username");

		if (username != null) {
			// 🔥 UPDATE LOGOUT TIME
			userRepository.updateLogoutTime(username);
		}

		Long auditId = (Long) session.getAttribute("auditId");

		if (auditId != null) {
			// 🔥 UPDATE LOGOUT TIME
			auditRepository.updateLogoutTime(auditId);
		}

		session.invalidate();

		return "redirect:/login";
	}
}