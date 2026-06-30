package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.entity.CertificateContactEntity;

@Service
public class ExportService {

    public byte[] exportExcel(List<String> numbers, String status) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(status);

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("S.No");
            header.createCell(1).setCellValue("Mobile Number");
            header.createCell(2).setCellValue("Status");

            int rowNum = 1;
            
            System.out.println("Exporting " + numbers.size() + " records");

            for (String mobile : numbers) {

                Row row = sheet.createRow(rowNum);

                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(mobile);
                row.createCell(2).setCellValue(status);

                rowNum++;
                
                
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public File generateCertificateSuccessExcel(
            List<CertificateContactEntity> contacts)
            throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("Success");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("S.No");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Mobile");
        header.createCell(3).setCellValue("Certificate");
        header.createCell(4).setCellValue("Status");
        header.createCell(5).setCellValue("Generated Time");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        int rowNum = 1;

        for (CertificateContactEntity contact : contacts) {

            Row row = sheet.createRow(rowNum);

            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(contact.getName());
            row.createCell(2).setCellValue(contact.getMobile());
            row.createCell(3).setCellValue(contact.getCertificateFile());
            row.createCell(4).setCellValue(contact.getStatus());

            if (contact.getGeneratedTime() != null) {

                row.createCell(5).setCellValue(
                        contact.getGeneratedTime().format(formatter));

            }

            rowNum++;

        }

        for (int i = 0; i < 6; i++) {

            sheet.autoSizeColumn(i);

        }

        File file = new File("generated/Success.xlsx");

        try (FileOutputStream fos = new FileOutputStream(file)) {

            workbook.write(fos);

        }

        workbook.close();

        return file;

    }
    
    public File generateCertificateFailedExcel(
            List<CertificateContactEntity> contacts)
            throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("Failed");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("S.No");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Mobile");
        header.createCell(3).setCellValue("Reason");

        int rowNum = 1;

        for (CertificateContactEntity contact : contacts) {

            Row row = sheet.createRow(rowNum);

            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(contact.getName());
            row.createCell(2).setCellValue(contact.getMobile());
            row.createCell(3).setCellValue(contact.getRemarks());

            rowNum++;

        }

        for (int i = 0; i < 4; i++) {

            sheet.autoSizeColumn(i);

        }

        File file = new File("generated/Failed.xlsx");

        try (FileOutputStream fos = new FileOutputStream(file)) {

            workbook.write(fos);

        }

        workbook.close();

        return file;

    }
    
    public File generateCertificatesZip(File certificateFolder)
            throws Exception {

        File zipFile = new File("generated/Certificates.zip");

        try (ZipOutputStream zos =
                     new ZipOutputStream(
                             new FileOutputStream(zipFile))) {

            File[] files = certificateFolder.listFiles();

            if (files != null) {

                for (File file : files) {

                    if (!file.isFile()) {
                        continue;
                    }

                    zos.putNextEntry(new ZipEntry(file.getName()));

                    FileInputStream fis =
                            new FileInputStream(file);

                    byte[] buffer = new byte[4096];

                    int len;

                    while ((len = fis.read(buffer)) > 0) {

                        zos.write(buffer, 0, len);

                    }

                    fis.close();

                    zos.closeEntry();

                }

            }

        }

        return zipFile;

    }
    
    public void deleteCertificateReports() {

        deleteFile("generated/Success.xlsx");

        deleteFile("generated/Failed.xlsx");

        deleteFile("generated/Certificates.zip");

    }
    
    private void deleteFile(String fileName) {

        File file = new File(fileName);

        if (file.exists()) {

            file.delete();

        }

    }
}