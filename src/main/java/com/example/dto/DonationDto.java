package com.example.dto;

import java.io.File;
import java.time.LocalDate;

public class DonationDto {
	
    private String name;

    private String mobile;

    private Double amount;

    private LocalDate  paymentReceivedDate;

    private LocalDate  donationDate;

    private String paymentMode;

    private String purpose;

    private String remarks;

    // Generated Files

    private File imageFile;

    private File pdfFile;

    // Status

    private boolean generated;

    private boolean whatsappSent;

    private String errorMessage;
    
    private String receiptNo ;

    public DonationDto() {
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentReceivedDate() {
		return paymentReceivedDate;
	}

	public void setPaymentReceivedDate(LocalDate paymentReceivedDate) {
		this.paymentReceivedDate = paymentReceivedDate;
	}

	public LocalDate getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(LocalDate donationDate) {
		this.donationDate = donationDate;
	}

	public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public File getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isWhatsappSent() {
        return whatsappSent;
    }

    public void setWhatsappSent(boolean whatsappSent) {
        this.whatsappSent = whatsappSent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "DonationDto{" +
                "receiptNo='" + receiptNo + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", amount=" + amount +
                ", paymentReceivedDate='" + paymentReceivedDate + '\'' +
                ", donationDate='" + donationDate + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", purpose='" + purpose + '\'' +
                ", remarks='" + remarks + '\'' +
                ", generated=" + generated +
                ", whatsappSent=" + whatsappSent +
                '}';
    }
}