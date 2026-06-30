package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.dto.DonationDto;

@Repository
public class DonationReceiptRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long getNextSequence() {

        return jdbcTemplate.queryForObject(
                "SELECT DONATION_RECEIPT_SEQ.NEXTVAL FROM DUAL",
                Long.class);
    }

    public void save(DonationDto dto) {

        String sql = """
            INSERT INTO DONATION_RECEIPTS
            (
                RECEIPT_NO,
                DONOR_NAME,
                MOBILE,
                AMOUNT,
                PAYMENT_RECEIVED_DATE,
                DONATION_DATE,
                PAYMENT_MODE,
                PURPOSE,
                REMARKS,
                RECEIPT_PATH,
                PDF_PATH,
                WHATSAPP_SENT,
                CREATED_DATE
            )
            VALUES
            (
                ?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE
            )
            """;

        jdbcTemplate.update(

                sql,

                dto.getReceiptNo(),

                dto.getName(),

                dto.getMobile(),

                dto.getAmount(),

                java.sql.Date.valueOf(dto.getPaymentReceivedDate()),

                java.sql.Date.valueOf(dto.getDonationDate()),

                dto.getPaymentMode(),

                dto.getPurpose(),

                dto.getRemarks(),

                dto.getImageFile() == null ? null :
                        dto.getImageFile().getAbsolutePath(),

                dto.getPdfFile() == null ? null :
                        dto.getPdfFile().getAbsolutePath(),

                dto.isWhatsappSent() ? "Y" : "N"

        );
    }

}