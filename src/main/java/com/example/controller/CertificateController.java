package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

import com.example.dto.CertificateProgressResponse;
import com.example.entity.CertificateContactEntity;
import com.example.service.CertificateProgressService;
import com.example.service.CertificateService;
import com.example.service.ExcelService;
import com.example.service.PdfService;
import com.example.service.WhatsAppService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CertificateController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    private CertificateProgressService progressService;

    /**
     * Certificate Home Screen
     */
    @GetMapping("/certificate")
    public String certificateHome() {

        return "CertificateGenerator";

    }

    /**
     * Preview Uploaded Excel
     */
    @PostMapping("/certificate-preview")
    public String certificatePreview(

            @RequestParam("excel")
            MultipartFile excel,

            @RequestParam("certificateDate")
            String certificateDate,

            @RequestParam("message")
            String message,

            HttpSession session,

            Model model) {

        try {

            List<CertificateContactEntity> contacts =
                    excelService.readCertificateExcel(excel);

            session.setAttribute("certificateContacts", contacts);

            session.setAttribute("certificateDate", certificateDate);

            session.setAttribute("certificateMessage", message);

            model.addAttribute("contacts", contacts);

            model.addAttribute("totalContacts", contacts.size());

            model.addAttribute("certificateDate", certificateDate);

            model.addAttribute("message", message);

            return "CertificatePreview";

        } catch (IOException e) {

            model.addAttribute(
                    "error",
                    "Unable to read uploaded Excel.");

            return "CertificateGenerator";

        }

    }
    
    /**
     * Generate Certificates Only
     */
    @PostMapping("/generate-certificates")
    public String generateCertificates(

            HttpSession session,

            Model model) {

        @SuppressWarnings("unchecked")
        List<CertificateContactEntity> contacts =
                (List<CertificateContactEntity>)
                        session.getAttribute("certificateContacts");

        String certificateDate =
                (String) session.getAttribute("certificateDate");
        
        System.out.println("contacts :::: "+contacts.get(0));

        if (contacts == null || contacts.isEmpty()) {

            model.addAttribute(
                    "error",
                    "No Contacts Found.");

            return "CertificateGenerator";

        }

        progressService.start(contacts.size());

        new Thread(() -> {

            try {

                certificateService.generateCertificates(
                        contacts,
                        certificateDate);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }).start();

        return "CertificateProgress";

    }

    /**
     * Generate Certificates & Send WhatsApp
     */
    @PostMapping("/generate-send-certificates")
    public String generateAndSend(

            HttpSession session,

            Model model) {
    	
        @SuppressWarnings("unchecked")
        List<CertificateContactEntity> contacts =
                (List<CertificateContactEntity>)
                        session.getAttribute("certificateContacts");

        String certificateDate =
                (String) session.getAttribute("certificateDate");

        String message =
                (String) session.getAttribute("certificateMessage");

        if (contacts == null || contacts.isEmpty()) {

            model.addAttribute(
                    "error",
                    "No Contacts Found.");

            return "CertificateGenerator";

        }

        progressService.start(contacts.size());

        new Thread(() -> {

            try {

                certificateService.generateAndSend(

                        contacts,

                        certificateDate,

                        message);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }).start();

        return "CertificateProgress";

    }
    
    /**
     * Progress API
     */
    @GetMapping("/certificate-progress")
    @ResponseBody
    public CertificateProgressResponse getProgress() {

        CertificateProgressResponse response =
                new CertificateProgressResponse();

        response.setTotal(progressService.getTotal());

        response.setCompleted(progressService.getCompleted());

        response.setSuccess(progressService.getSuccess());

        response.setFailed(progressService.getFailed());

        response.setPercentage(progressService.getPercentage());

        response.setCurrentName(progressService.getCurrentName());

        response.setCurrentPdf(progressService.getCurrentPdf());

        response.setStatus(progressService.getStatus());
        
        response.setCurrentMobile(
                progressService.getCurrentMobile());

        response.setRunning(
                progressService.isRunning());

        response.setCurrentDate(progressService.getCurrentDate());

        response.setCompletedStatus(
                !progressService.isRunning());

        return response;

    }

    /**
     * Download Generated ZIP
     */
    @GetMapping("/download-certificates")
    public ResponseEntity<Resource> downloadCertificates()
            throws Exception {

        File zipFile =
                certificateService.getGeneratedZip();

        Resource resource =
                new FileSystemResource(zipFile);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Certificates.zip")

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)

                .body(resource);

    }

    /**
     * Download Success Excel
     */
    @GetMapping("/cert-download-success")
    public ResponseEntity<Resource> CertificateDownloadSuccess()
            throws Exception {

        Path path =
                Paths.get(
                        "generated/success.xlsx");

        Resource resource =
                new FileSystemResource(path);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Success.xlsx")

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)

                .body(resource);

    }

    /**
     * Download Failed Excel
     */
    @GetMapping("/cert-download-failed")
    public ResponseEntity<Resource> CertificateDownloadFailed()
            throws Exception {

        Path path =
                Paths.get(
                        "generated/failed.xlsx");

        Resource resource =
                new FileSystemResource(path);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Failed.xlsx")

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM)

                .body(resource);

    }

    /**
     * Reset Progress
     */
    @GetMapping("/certificate-reset")
    @ResponseBody
    public String resetProgress() {

        progressService.reset();

        return "SUCCESS";

    }

}
