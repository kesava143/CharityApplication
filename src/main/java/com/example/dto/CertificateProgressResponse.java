package com.example.dto;

public class CertificateProgressResponse {

    private int total;

    private int completed;

    private int success;

    private int failed;

    private int percentage;

    private String currentName;

    private String currentPdf;

    private String currentMobile;

    private String currentDate;

    private String status;

    private boolean completedStatus;

    private boolean running;

    public CertificateProgressResponse() {

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

    public String getCurrentPdf() {
        return currentPdf;
    }

    public void setCurrentPdf(String currentPdf) {
        this.currentPdf = currentPdf;
    }

    public synchronized String getCurrentMobile() {
        return currentMobile;
    }

    public synchronized void setCurrentMobile(String currentMobile) {
        this.currentMobile = currentMobile;
        
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompletedStatus() {
        return completedStatus;
    }

    public void setCompletedStatus(boolean completedStatus) {
        this.completedStatus = completedStatus;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

	@Override
	public String toString() {
		return "CertificateProgressResponse [total=" + total + ", completed=" + completed + ", success=" + success
				+ ", failed=" + failed + ", percentage=" + percentage + ", currentName=" + currentName + ", currentPdf="
				+ currentPdf + ", currentMobile=" + currentMobile + ", currentDate=" + currentDate + ", status="
				+ status + ", completedStatus=" + completedStatus + ", running=" + running + "]";
	}

    
}