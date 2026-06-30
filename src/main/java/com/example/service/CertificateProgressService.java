package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class CertificateProgressService {

    private int total;
    private int completed;
    private int success;
    private int failed;

    private int percentage;

    private boolean running;

    private String currentName = "";

    private String currentPdf = "";

    private String currentDate = "";

    private String status = "";
    
    private String currentMobile = "";

    public String getCurrentMobile() {
		return currentMobile;
	}

	public void setCurrentMobile(String currentMobile) {
		this.currentMobile = currentMobile;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
     * Start Progress
     */
    public synchronized void start(int totalContacts) {

        this.total = totalContacts;

        this.completed = 0;

        this.success = 0;

        this.failed = 0;

        this.percentage = 0;

        this.running = true;

        this.currentName = "";

        this.currentPdf = "";

        this.currentDate = "";

        this.status = "Starting...";

    }

    /**
     * Reset
     */
    public synchronized void reset() {

        total = 0;

        completed = 0;

        success = 0;

        failed = 0;

        percentage = 0;

        running = false;

        currentName = "";

        currentPdf = "";

        currentDate = "";

        status = "";

    }

    /**
     * Success
     */
    public synchronized void markSuccess() {

        success++;

        completed++;

        calculate();

    }

    /**
     * Failed
     */
    public synchronized void markFailed() {

        failed++;

        completed++;

        calculate();

    }

    /**
     * Calculate Percentage
     */
    private void calculate() {

        if (total == 0) {

            percentage = 0;

            return;

        }

        percentage = (completed * 100) / total;

        if (completed >= total) {

            running = false;

            status = "Completed Successfully";

        }

    }

    /**
     * Current Name
     */
    public synchronized void setCurrentName(String currentName) {

        this.currentName = currentName;

    }

    /**
     * Current PDF
     */
    public synchronized void setCurrentPdf(String currentPdf) {

        this.currentPdf = currentPdf;

    }

    /**
     * Current Date
     */
    public synchronized void setCurrentDate(String currentDate) {

        this.currentDate = currentDate;

    }

    /**
     * Current Status
     */
    public synchronized void setStatus(String status) {

        this.status = status;

    }

    /**
     * Getters
     */

    public synchronized int getTotal() {

        return total;

    }

    public synchronized int getCompleted() {

        return completed;

    }

    public synchronized int getSuccess() {

        return success;

    }

    public synchronized int getFailed() {

        return failed;

    }

    public synchronized int getPercentage() {

        return percentage;

    }

    public synchronized boolean isRunning() {

        return running;

    }

    public synchronized String getCurrentName() {

        return currentName;

    }

    public synchronized String getCurrentPdf() {

        return currentPdf;

    }

    public synchronized String getCurrentDate() {

        return currentDate;

    }

    public synchronized String getStatus() {

        return status;

    }

	@Override
	public String toString() {
		return "CertificateProgressService [total=" + total + ", completed=" + completed + ", success=" + success
				+ ", failed=" + failed + ", percentage=" + percentage + ", running=" + running + ", currentName="
				+ currentName + ", currentPdf=" + currentPdf + ", currentDate=" + currentDate + ", status=" + status
				+ ", currentMobile=" + currentMobile + "]";
	}
    
    

}