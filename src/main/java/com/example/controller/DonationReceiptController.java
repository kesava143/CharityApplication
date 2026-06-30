package com.example.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dto.DonationProgressResponse;
import com.example.service.DonationReceiptService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/donation")
public class DonationReceiptController {

    @Autowired
    private DonationReceiptService donationReceiptService;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    /**
     * Open Donation Receipt Page
     */
    @GetMapping
    public String donationReceipt(HttpSession session,
                                  Model model) {

        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        return "donationReceipt";
    }

    /**
     * Upload Excel
     */
    @PostMapping("/uploadExcel")
    @ResponseBody
    public String uploadExcel(
            @RequestParam("excelFile")
            MultipartFile excelFile,
            HttpSession session) {

        try {

            donationReceiptService.readExcel(excelFile);

            return "SUCCESS";

        } catch (Exception e) {

            e.printStackTrace();

            return "FAILED";
        }
    }

    /**
     * Generate Receipts
     */
    @PostMapping("/generate")
    @ResponseBody
    public String generateReceipts(
            @RequestParam(defaultValue = "JPG")
            String outputType,

            @RequestParam(defaultValue = "true")
            boolean sendWhatsapp,

            HttpSession session) {

        executor.submit(() -> {

            try {

                donationReceiptService.generateReceipts(
                        outputType,
                        sendWhatsapp);

            } catch (Exception e) {

                e.printStackTrace();

            }

        });

        return "STARTED";
    }

    /**
     * Progress Bar AJAX
     */
    @GetMapping("/progress")
    @ResponseBody
    public DonationProgressResponse progress() {

        return donationReceiptService.getProgress();

    }

    /**
     * Cancel Current Process
     */
    @PostMapping("/cancel")
    @ResponseBody
    public String cancel() {

        donationReceiptService.cancel();

        return "CANCELLED";
    }

    /**
     * Reset
     */
    @PostMapping("/reset")
    @ResponseBody
    public String reset() {

        donationReceiptService.reset();

        return "SUCCESS";
    }

}