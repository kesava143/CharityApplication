package com.example.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.DonationDto;

@Service
public class DonationWhatsAppService {

    @Autowired
    private DonationWhatsAppWebService whatsAppService;

    //---------------------------------------------------------
    // Send Receipt
    //---------------------------------------------------------

    public boolean sendReceipt(
            DonationDto donation,
            String format) {

        try {

            File attachment;

            if ("PDF".equalsIgnoreCase(format)) {

                attachment = donation.getPdfFile();

            } else {

                attachment = donation.getImageFile();

            }

            if (attachment == null || !attachment.exists()) {

                throw new RuntimeException(
                        "Receipt not found.");

            }

            String message =
                    buildMessage(donation);

            boolean status =
                    whatsAppService.sendMessageWithAttachment(

                            donation.getMobile(),

                            message,

                            attachment);

            donation.setWhatsappSent(status);

            return status;

        } catch (Exception e) {

            donation.setWhatsappSent(false);

            donation.setErrorMessage(
                    e.getMessage());

            e.printStackTrace();

            return false;

        }

    }

    //---------------------------------------------------------
    // Build WhatsApp Message
    //---------------------------------------------------------

    private String buildMessage(
            DonationDto donation) {

        StringBuilder message =
                new StringBuilder();

        message.append("Dear *")
               .append(donation.getName())
               .append(" Garu*,\n\n");

        message.append(
                "Greetings from *Helping Hearts And Supporting Hands Charitable Trust* ❤️\n\n");

        message.append(
                "Thank you for your generous contribution towards our charitable activities.\n\n");

        message.append(
                "📄 Please find attached your *Donation Receipt*.\n\n");

        message.append("💰 Donation Amount : *₹")
               .append(String.format("%,.2f",
                       donation.getAmount()))
               .append("*\n");

        message.append("📅 Donation Date : *")
               .append(donation.getDonationDate())
               .append("*\n");

        message.append("💳 Payment Mode : *")
               .append(donation.getPaymentMode())
               .append("*\n\n");

        if (donation.getPurpose() != null &&
                !donation.getPurpose().isBlank()) {

            message.append("🎯 Purpose : ")
                   .append(donation.getPurpose())
                   .append("\n\n");

        }

        message.append(
                "Your support enables us to continue serving those in need and making a positive impact in society.\n\n");

        message.append(
                "With sincere gratitude,\n\n");

        message.append(
                "*Helping Hearts And Supporting Hands Charitable Trust*\n");

        message.append("☎ 9885795766\n");

        message.append("🌐 https://www.hhash.co.in");

        return message.toString();

    }

}