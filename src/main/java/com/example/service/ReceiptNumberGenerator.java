package com.example.service;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.repository.DonationReceiptRepository;

@Service
public class ReceiptNumberGenerator {

    @Autowired
    private DonationReceiptRepository  repository;

    public synchronized String generateReceiptNo() {

        int year = Year.now().getValue();

        Long next = repository.getNextSequence();
        
        System.out.println("next :::::: "+next);
        
        //Thread.dumpStack();

        return String.format(
                "HHASH/%d/%06d",
                year,
                next);

    }

}
