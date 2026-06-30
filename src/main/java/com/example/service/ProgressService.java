package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    private int total;
    private int completedCount;
    private int success;
    private int failed;
    private int percentage;

    private boolean running;
    private boolean completed;

    private String currentNumber;

    public synchronized void start(int totalContacts) {

        this.total = totalContacts;
        this.completedCount = 0;
        this.success = 0;
        this.failed = 0;
        this.percentage = 0;

        this.running = true;
        this.completed = false;

        this.currentNumber = "";

    }

    public synchronized void setCurrentNumber(String number) {

        this.currentNumber = number;

    }

    public synchronized void markSuccess() {

        success++;
        completedCount++;

        calculate();

    }

    public synchronized void markFailed() {

        failed++;
        completedCount++;

        calculate();

    }

    private void calculate() {

        if (total == 0) {

            percentage = 0;
            return;

        }

        percentage = (completedCount * 100) / total;

        if (completedCount >= total) {

            running = false;
            completed = true;

        }

    }

    public synchronized void reset() {

        total = 0;
        completedCount = 0;
        success = 0;
        failed = 0;
        percentage = 0;
        running = false;
        completed = false;
        currentNumber = "";

    }

    public synchronized int getTotal() {
        return total;
    }

    public synchronized int getCompleted() {
        return completedCount;
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

    public synchronized String getCurrentNumber() {
        return currentNumber;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized boolean isCompleted() {
        return completed;
    }

}