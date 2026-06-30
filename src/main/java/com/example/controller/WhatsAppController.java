package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.WhatsAppContactEntity;
import com.example.service.ExcelService;
import com.example.service.ExportService;
import com.example.service.ProgressService;
import com.example.service.WhatsAppService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class WhatsAppController {
	
	@Autowired
	private WhatsAppService whatsAppService;
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private ProgressService progressService;
	
	@Autowired
	private ExportService exportService;

	@GetMapping("/WhatsApp")
	public String WhatsApp(HttpServletRequest request, HttpSession session) {

		return "WhatsApp";
	}
	
	@GetMapping("/open-whatsapp")
	@ResponseBody
	public String openWhatsapp() {

	    whatsAppService.openWhatsapp();

	    return "WhatsApp Opened";

	}
	
	@GetMapping("/test-whatsapp")
	@ResponseBody
	public String testWhatsapp() {

	    whatsAppService.openWhatsapp();

	    String[] numbers = {
	        "7799161287",
	        "9030088448",
	        "9885795766"
	    };

	    String message = """
	            Hello from Helping Hearts.

	            This is a Bulk WhatsApp Test.

	            Regards,
	            Helping Hearts
	            """;

	    whatsAppService.sendBulkMessages(numbers, message);

	    return "Bulk Messages Sent";
	}
	
	@PostMapping("/preview-whatsapp")
	public String previewWhatsapp(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam("message") String message,
	        HttpSession session,
	        Model model) {

	    try {

	        if (file.isEmpty()) {

	            model.addAttribute("error", "Please select an Excel file.");
	            return "WhatsApp";
	        }

	        List<WhatsAppContactEntity> contacts =
	                excelService.readExcel(file);

	        if (contacts.isEmpty()) {

	            model.addAttribute("error",
	                    "No valid contacts found.");

	            return "WhatsApp";
	        }

	        // Store for sending later
	        session.setAttribute("contacts", contacts);
	        session.setAttribute("message", message);

	        model.addAttribute("contacts", contacts);
	        model.addAttribute("message", message);
	        model.addAttribute("totalContacts", contacts.size());

	        return "WhatsAppPreview";

	    } catch (Exception e) {

	        e.printStackTrace();

	        model.addAttribute("error", e.getMessage());

	        return "WhatsApp";
	    }

	}
	
	@PostMapping("/send-whatsapp")
	@ResponseBody
	public String sendWhatsapp(
	        @RequestParam("mobiles") List<String> mobiles,
	        @RequestParam("message") String message,
	        HttpSession session) {

	    @SuppressWarnings("unchecked")
	    List<WhatsAppContactEntity> allContacts =
	            (List<WhatsAppContactEntity>) session.getAttribute("contacts");

	    if (allContacts == null) {
	        return "NO_CONTACTS";
	    }

	    List<WhatsAppContactEntity> selectedContacts = allContacts.stream()
	            .filter(c -> mobiles.contains(c.getMobile()))
	            .toList();

	    new Thread(() -> {

	        whatsAppService.sendBulkMessages(selectedContacts, message);

	    }).start();

	    return "STARTED";
	}
	
	@GetMapping("/progress")
	@ResponseBody
	public Map<String, Object> getProgress() {

		Map<String,Object> map = new HashMap<>();

		map.put("total", progressService.getTotal());
		map.put("completed", progressService.getCompleted());
		map.put("success", progressService.getSuccess());
		map.put("failed", progressService.getFailed());
		map.put("percentage", progressService.getPercentage());
		map.put("currentNumber", progressService.getCurrentNumber());
		map.put("running", progressService.isRunning());
		map.put("completedStatus", progressService.isCompleted());

		return map;
	}
	
	@GetMapping("/download-success")
	public ResponseEntity<byte[]> downloadSuccess() {

	    byte[] excel =
	            exportService.exportExcel(
	                    whatsAppService.getSuccessNumbers(),
	                    "SUCCESS");

	    return ResponseEntity.ok()

	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=Success.xlsx")

	            .contentType(
	                    MediaType.APPLICATION_OCTET_STREAM)

	            .body(excel);

	}
	
	@GetMapping("/download-failed")
	public ResponseEntity<byte[]> downloadFailed() {

	    byte[] excel =
	            exportService.exportExcel(
	                    whatsAppService.getFailedNumbers(),
	                    "FAILED");

	    return ResponseEntity.ok()

	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=Failed.xlsx")

	            .contentType(
	                    MediaType.APPLICATION_OCTET_STREAM)

	            .body(excel);

	}
	
}
