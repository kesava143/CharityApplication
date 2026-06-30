package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.CertificateContactEntity;
import com.example.entity.WhatsAppContactEntity;

@Service
public class ExcelService {

    public List<WhatsAppContactEntity> readExcel(MultipartFile file) throws Exception {

        List<WhatsAppContactEntity> contacts = new ArrayList<>();

        Set<String> uniqueNumbers = new HashSet<>();

        InputStream inputStream = file.getInputStream();

        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();

        if (!rows.hasNext()) {
            workbook.close();
            return contacts;
        }

        Row headerRow = rows.next();

        int mobileColumn = -1;
        int nameColumn = -1;

        DataFormatter formatter = new DataFormatter();

        // Detect Columns Automatically
        for (Cell cell : headerRow) {

            String header = formatter.formatCellValue(cell)
                    .trim()
                    .toLowerCase();

            if (header.contains("mobile")
                    || header.contains("phone")
                    || header.contains("contact")) {

                mobileColumn = cell.getColumnIndex();
            }

            if (header.contains("name")) {

                nameColumn = cell.getColumnIndex();
            }

        }

        if (mobileColumn == -1) {

            workbook.close();

            throw new RuntimeException("Mobile/Phone column not found in Excel.");

        }

        while (rows.hasNext()) {

            Row row = rows.next();

            String mobile = formatter.formatCellValue(row.getCell(mobileColumn));

            if (mobile == null)
                continue;

            mobile = mobile.trim();

            mobile = mobile.replace("+91", "");

            mobile = mobile.replaceAll("[^0-9]", "");

            if (mobile.length() > 10) {

                mobile = mobile.substring(mobile.length() - 10);

            }

            if (mobile.length() != 10)
                continue;

            if (uniqueNumbers.contains(mobile))
                continue;

            uniqueNumbers.add(mobile);

            String name = "";

            if (nameColumn != -1) {

                name = formatter.formatCellValue(row.getCell(nameColumn));

            }

            WhatsAppContactEntity contact = new WhatsAppContactEntity();

            contact.setName(name);

            contact.setMobile(mobile);

            contacts.add(contact);

        }

        workbook.close();

        return contacts;

    }
    
    
    public List<CertificateContactEntity> readCertificateExcel(
            MultipartFile file) throws IOException {

        List<CertificateContactEntity> contacts = new ArrayList<>();

        Set<String> mobileSet = new HashSet<>();

        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String name = formatter.formatCellValue(row.getCell(0)).trim();

                String mobile = formatter.formatCellValue(row.getCell(1)).trim();

                if (name.isBlank() || mobile.isBlank()) {
                    continue;
                }

                mobile = normalizeMobile(mobile);

                if (mobile.length() != 10) {
                    continue;
                }

                if (mobileSet.contains(mobile)) {
                    continue;
                }

                mobileSet.add(mobile);

                CertificateContactEntity contact =
                        new CertificateContactEntity();

                contact.setName(name);

                contact.setMobile(mobile);

                contact.setStatus("READY");

                contact.setGeneratedTime(LocalDateTime.now());

                contacts.add(contact);

            }

        }

        return contacts;

    }
    
    private String normalizeMobile(String mobile) {

        mobile = mobile.replaceAll("\\s+", "");

        mobile = mobile.replaceAll("-", "");

        mobile = mobile.replace("(", "");

        mobile = mobile.replace(")", "");

        if (mobile.startsWith("+91")) {

            mobile = mobile.substring(3);

        }

        if (mobile.startsWith("91") && mobile.length() == 12) {

            mobile = mobile.substring(2);

        }

        if (mobile.startsWith("0")) {

            mobile = mobile.substring(1);

        }

        mobile = mobile.replaceAll("[^0-9]", "");

        return mobile;

    }
    
    public boolean isCertificateExcelValid(
            MultipartFile file) {

        try {

            List<CertificateContactEntity> contacts =
                    readCertificateExcel(file);

            return !contacts.isEmpty();

        } catch (Exception e) {

            return false;

        }

    }
    
    public int getCertificateContactCount(
            MultipartFile file) {

        try {

            return readCertificateExcel(file).size();

        } catch (Exception e) {

            return 0;

        }

    }
    
    public List<CertificateContactEntity> previewCertificateExcel(
            MultipartFile file)
            throws IOException {

        return readCertificateExcel(file);

    }

}