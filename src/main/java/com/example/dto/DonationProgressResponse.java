package com.example.dto;

public class DonationProgressResponse {

    private int total;

    private int completed;

    private int success;

    private int failed;

    private int percentage;

    private String currentName;

    private String currentMobile;

    private String currentStatus;

    private boolean completedProcess;

    private boolean cancelled;

    private String message;

    public DonationProgressResponse() {
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

    public String getCurrentMobile() {
        return currentMobile;
    }

    public void setCurrentMobile(String currentMobile) {
        this.currentMobile = currentMobile;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isCompletedProcess() {
        return completedProcess;
    }

    public void setCompletedProcess(boolean completedProcess) {
        this.completedProcess = completedProcess;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}