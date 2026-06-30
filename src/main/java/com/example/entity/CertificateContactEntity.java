package com.example.entity;

import java.time.LocalDateTime;

public class CertificateContactEntity {

    private String name;

    private String mobile;

    private String certificateFile;

    private String message;

    private String status;

    private String remarks;

    private boolean whatsappSent;

    private LocalDateTime generatedTime;

    public CertificateContactEntity() {
    }

    public CertificateContactEntity(String name, String mobile) {

        this.name = name;

        this.mobile = mobile;

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

    public String getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(String certificateFile) {
        this.certificateFile = certificateFile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isWhatsappSent() {
        return whatsappSent;
    }

    public void setWhatsappSent(boolean whatsappSent) {
        this.whatsappSent = whatsappSent;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

}