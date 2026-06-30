package com.example.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.DonationDto;
import com.example.dto.DonationProgressResponse;
import com.example.entity.DonationReceiptEntity;
import com.example.repository.DonationReceiptRepository;
import com.example.repository.DonationReceiptSaveRepository;

@Service
public class DonationReceiptService {

    @Autowired
    private DonationExcelReader donationExcelReader;

    @Autowired
    private DonationReceiptImageGenerator imageGenerator;

    @Autowired
    private DonationReceiptPdfGenerator pdfGenerator;

    @Autowired
    private DonationWhatsAppService whatsappService;
    
    @Autowired
    private DonationReceiptSaveRepository donationReceiptSaveRepository;

    //---------------------------------------------------------
    // Working Data
    //---------------------------------------------------------

    private final List<DonationDto> donations =
            new ArrayList<>();

    //---------------------------------------------------------
    // Progress Variables
    //---------------------------------------------------------

    private volatile int total = 0;

    private volatile int completed = 0;

    private volatile int success = 0;

    private volatile int failed = 0;

    private volatile int percentage = 0;

    private volatile String currentName = "";

    private volatile boolean cancelled = false;
    
    private volatile String currentMobile = "";

    private volatile String currentStatus = "Excel Uploaded";

    //---------------------------------------------------------
    // Output Folders
    //---------------------------------------------------------

    private static final String ROOT_FOLDER =
            "generated/donation-receipts";

    private static final String JPG_FOLDER =
            ROOT_FOLDER + "/jpg";

    private static final String PNG_FOLDER =
            ROOT_FOLDER + "/png";

    private static final String PDF_FOLDER =
            ROOT_FOLDER + "/pdf";

    private static final String REPORT_FOLDER =
            ROOT_FOLDER + "/reports";

    //---------------------------------------------------------
    // Constructor
    //---------------------------------------------------------

    public DonationReceiptService() {

        createFolders();

    }

    //---------------------------------------------------------
    // Create Output Directories
    //---------------------------------------------------------

    private void createFolders() {

        try {

            Files.createDirectories(Paths.get(ROOT_FOLDER));

            Files.createDirectories(Paths.get(JPG_FOLDER));

            Files.createDirectories(Paths.get(PNG_FOLDER));

            Files.createDirectories(Paths.get(PDF_FOLDER));

            Files.createDirectories(Paths.get(REPORT_FOLDER));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create donation folders.",
                    e);

        }

    }
    
    public void generateReceipts(
            String outputType,
            boolean sendWhatsapp) throws Exception {
    	
    	total = donations.size();

    	completed = 0;

    	success = 0;

    	failed = 0;

    	percentage = 0;
    	
    	cancelled = false;

    	for (DonationDto donation : donations) {

    	    if (cancelled) {

    	        System.out.println("Donation generation cancelled.");

    	        currentStatus = "Cancelled";

    	        break;
    	    }

    	    currentName = donation.getName();
    	    currentMobile = donation.getMobile();
    	    currentStatus = "Generating Receipt";

    	    try {

    	        // Generate Image
    	        imageGenerator.generateImage(
    	                donation,
    	                outputType);

    	        // Generate PDF
    	        if ("PDF".equalsIgnoreCase(outputType)) {

    	            pdfGenerator.generatePdf(donation);

    	        }

    	        currentStatus = "Saving";

    	        DonationReceiptEntity entity = new DonationReceiptEntity();

    	        entity.setReceiptNo(donation.getReceiptNo());
    	        entity.setDonorName(donation.getName());
    	        entity.setMobile(donation.getMobile());
    	        entity.setAmount(new BigDecimal(donation.getAmount()));

    	        entity.setDonationDate(
    	                donation.getDonationDate() == null
    	                        ? null
    	                        : java.sql.Date.valueOf(donation.getDonationDate()));

    	        entity.setPaymentReceivedDate(
    	                donation.getPaymentReceivedDate() == null
    	                        ? null
    	                        : java.sql.Date.valueOf(donation.getPaymentReceivedDate()));

    	        entity.setPaymentMode(donation.getPaymentMode());
    	        entity.setPurpose(donation.getPurpose());
    	        entity.setRemarks(donation.getRemarks());

    	        entity.setWhatsappSent(sendWhatsapp ? "Y" : "N");

    	        entity.setCreatedDate(new java.util.Date());
    	        
    	        entity.setStatus("COMPLETED");

    	        donationReceiptSaveRepository.save(entity);

    	        System.out.println("Saved Receipt : " + entity.getReceiptNo());

    	        if (sendWhatsapp) {

    	            currentStatus = "Sending WhatsApp";

    	            whatsappService.sendReceipt(
    	                    donation,
    	                    outputType);
    	        }

    	        success++;

    	        currentStatus = "Completed";

    	    } catch (Exception e) {

    	        failed++;

    	        currentStatus = "Failed";

    	        e.printStackTrace();
    	    }

    	    completed++;

    	    percentage = (completed * 100) / total;
    	}

    	currentStatus = cancelled ? "Cancelled" : "Completed";
    	
    }

    //---------------------------------------------------------
    // Clear Previous Data
    //---------------------------------------------------------

    public void reset() {

        donations.clear();

        total = 0;

        completed = 0;

        success = 0;

        failed = 0;

        percentage = 0;

        currentName = "";

        cancelled = false;
        
        currentMobile = "";
        currentStatus = "Waiting...";

    }

    //---------------------------------------------------------
    // Cancel Process
    //---------------------------------------------------------

    public void cancel() {

        cancelled = true;

    }

    //---------------------------------------------------------
    // Check Cancel Status
    //---------------------------------------------------------

    public boolean isCancelled() {

        return cancelled;

    }

    //---------------------------------------------------------
    // Progress Response
    //---------------------------------------------------------

    public DonationProgressResponse getProgress() {

        DonationProgressResponse response =
                new DonationProgressResponse();

        response.setTotal(total);
        response.setCompleted(completed);
        response.setSuccess(success);
        response.setFailed(failed);
        response.setPercentage(percentage);

        response.setCurrentName(currentName);
        response.setCurrentMobile(currentMobile);
        response.setCurrentStatus(currentStatus);

        return response;

    }

    //---------------------------------------------------------
    // Getter
    //---------------------------------------------------------
    
    

    public List<DonationDto> getDonations() {

        return donations;

    }

	public DonationExcelReader getDonationExcelReader() {
		return donationExcelReader;
	}

	public void setDonationExcelReader(DonationExcelReader donationExcelReader) {
		this.donationExcelReader = donationExcelReader;
	}

	public DonationReceiptImageGenerator getImageGenerator() {
		return imageGenerator;
	}

	public void setImageGenerator(DonationReceiptImageGenerator imageGenerator) {
		this.imageGenerator = imageGenerator;
	}

	public DonationReceiptPdfGenerator getPdfGenerator() {
		return pdfGenerator;
	}

	public void setPdfGenerator(DonationReceiptPdfGenerator pdfGenerator) {
		this.pdfGenerator = pdfGenerator;
	}

	public DonationWhatsAppService getWhatsappService() {
		return whatsappService;
	}

	public void setWhatsappService(DonationWhatsAppService whatsappService) {
		this.whatsappService = whatsappService;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	public static String getRootFolder() {
		return ROOT_FOLDER;
	}

	public static String getJpgFolder() {
		return JPG_FOLDER;
	}

	public static String getPngFolder() {
		return PNG_FOLDER;
	}

	public static String getPdfFolder() {
		return PDF_FOLDER;
	}

	public static String getReportFolder() {
		return REPORT_FOLDER;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

    //---------------------------------------------------------
    // Part 2
    //---------------------------------------------------------
    // readExcel(MultipartFile excelFile)
	
	//---------------------------------------------------------
	// Read Excel
	//---------------------------------------------------------

	public List<DonationDto> readExcel(
	        MultipartFile excelFile) throws Exception {

	    if (excelFile == null || excelFile.isEmpty()) {
	    	
	    	reset();

		    donations.clear();

	        throw new RuntimeException(
	                "Please select an Excel file.");

	    }
	    
	    reset();

	    donations.clear();

	    

	    List<DonationDto> list =
	            donationExcelReader.read(excelFile);
	    
	    total = donations.size();

	    completed = 0;

	    success = 0;

	    failed = 0;

	    percentage = 0;

	    currentName = "";

	    currentMobile = "";

	    currentStatus = "Excel Uploaded";
	    
	    System.out.println("Records Loaded : " + total);

	    if (list == null || list.isEmpty()) {

	        throw new RuntimeException(
	                "No valid donation records found.");

	    }

	    donations.addAll(list);

	    total = donations.size();

	    return donations;

	}

	//---------------------------------------------------------
	// Donation List
	//---------------------------------------------------------

	public List<DonationDto> getDonationList() {

	    return donations;

	}

	//---------------------------------------------------------
	// Total Records
	//---------------------------------------------------------

	public int getTotalRecords() {

	    return donations.size();

	}

	//---------------------------------------------------------
	// Has Records
	//---------------------------------------------------------

	public boolean hasRecords() {

	    return !donations.isEmpty();

	}

	//---------------------------------------------------------
	// Clear Uploaded Data
	//---------------------------------------------------------

	public void clearDonationList() {

	    donations.clear();

	    total = 0;

	}

}