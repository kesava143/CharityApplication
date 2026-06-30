package com.example.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.DonationReceiptEntity;

@Repository
public interface DonationReceiptSaveRepository
        extends JpaRepository<DonationReceiptEntity, UUID> {

} //checking