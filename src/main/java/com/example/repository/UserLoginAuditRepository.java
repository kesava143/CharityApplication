package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.example.entity.UserLoginAuditEntity;

public interface UserLoginAuditRepository extends JpaRepository<UserLoginAuditEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE USER_LOGIN_AUDIT SET LOGOUT_TIME = SYSDATE WHERE ID = :id", nativeQuery = true)
    void updateLogoutTime(Long id);
}