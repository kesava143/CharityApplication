package com.example.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "DONATION_RECEIPTS")
public class DonationReceiptEntity {

	@Id
	@UuidGenerator
	@Column(name = "ID", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "RECEIPT_NO", length = 30)
	private String receiptNo;

	@Column(name = "DONOR_NAME", length = 200)
	private String donorName;

	@Column(name = "MOBILE", length = 15)
	private String mobile;

	@Column(name = "AMOUNT", precision = 12, scale = 2)
	private BigDecimal amount;

	@Temporal(TemporalType.DATE)
	@Column(name = "PAYMENT_RECEIVED_DATE")
	private Date paymentReceivedDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "DONATION_DATE")
	private Date donationDate;

	@Column(name = "PAYMENT_MODE", length = 30)
	private String paymentMode;

	@Column(name = "PURPOSE", length = 500)
	private String purpose;

	@Column(name = "REMARKS", length = 500)
	private String remarks;

	@Column(name = "RECEIPT_PATH", length = 500)
	private String receiptPath;

	@Column(name = "PDF_PATH", length = 500)
	private String pdfPath;

	@Column(name = "WHATSAPP_SENT", length = 1)
	private String whatsappSent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "STATUS", length = 30)
	private String status;

	public DonationReceiptEntity() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getDonorName() {
		return donorName;
	}

	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getPaymentReceivedDate() {
		return paymentReceivedDate;
	}

	public void setPaymentReceivedDate(Date paymentReceivedDate) {
		this.paymentReceivedDate = paymentReceivedDate;
	}

	public Date getDonationDate() {
		return donationDate;
	}

	public void setDonationDate(Date donationDate) {
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

	public String getReceiptPath() {
		return receiptPath;
	}

	public void setReceiptPath(String receiptPath) {
		this.receiptPath = receiptPath;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getWhatsappSent() {
		return whatsappSent;
	}

	public void setWhatsappSent(String whatsappSent) {
		this.whatsappSent = whatsappSent;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}