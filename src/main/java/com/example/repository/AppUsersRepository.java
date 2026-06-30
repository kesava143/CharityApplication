package com.example.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.AppUsersEntity;

import jakarta.transaction.Transactional;

public interface AppUsersRepository extends JpaRepository<AppUsersEntity, Long> {

    @Query("SELECT u FROM AppUsersEntity u WHERE u.username = :username AND u.password = :password AND u.recordStatus = 'ACTIVE'")
    AppUsersEntity login(@Param("username") String username,
                         @Param("password") String password);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE APP_USERS SET LOGIN_TIME = SYSDATE WHERE USERNAME = :username", nativeQuery = true)
    void updateLoginTime(@Param("username") String username);


    @Modifying
    @Transactional
    @Query(value = "UPDATE APP_USERS SET LOGOUT_TIME = SYSDATE WHERE USERNAME = :username", nativeQuery = true)
    void updateLogoutTime(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE AppUsersEntity u SET u.otp = :otp, u.otpExpiry = :expiry WHERE u.username = :username")
    void updateOtp(@Param("username") String username,
                   @Param("otp") String otp,
                   @Param("expiry") Date expiry);

    AppUsersEntity findByUsername(String username);
}