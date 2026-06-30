package com.example.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.example.dto.DonationDto;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class DonationReceiptPdfGenerator {

    private static final String PDF_FOLDER =
            "generated/donation-receipts/pdf/";

    public DonationReceiptPdfGenerator() {

        createFolder();

    }

    //-------------------------------------------------------
    // Create Folder
    //-------------------------------------------------------

    private void createFolder() {

        try {

            Files.createDirectories(
                    Paths.get(PDF_FOLDER));

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    //-------------------------------------------------------
    // Generate PDF
    //-------------------------------------------------------

    public File generatePdf(
            DonationDto donation)
            throws Exception {

        if (donation.getImageFile() == null) {

            throw new RuntimeException(
                    "Receipt Image not generated.");

        }

        File pdfFile =
                new File(
                        PDF_FOLDER
                                + sanitize(
                                        donation.getName())
                                + ".pdf");

        Document document =
                new Document(PageSize.A4);

        PdfWriter.getInstance(
                document,
                new FileOutputStream(pdfFile));

        document.open();

        Image image =
                Image.getInstance(
                        donation.getImageFile()
                                .getAbsolutePath());

        Rectangle page =
                document.getPageSize();

        image.scaleToFit(

                page.getWidth() - 40,

                page.getHeight() - 40);

        image.setAlignment(Image.ALIGN_CENTER);

        document.add(image);

        document.close();

        donation.setPdfFile(pdfFile);

        return pdfFile;

    }

    //-------------------------------------------------------
    // File Name
    //-------------------------------------------------------

    private String sanitize(
            String value) {

        if (value == null ||
                value.isBlank()) {

            return "Receipt";

        }

        return value.replaceAll(

                "[\\\\/:*?\"<>|]",

                "_");

    }

}