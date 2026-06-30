package com.example.service;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.DonationDto;

@Service
public class DonationExcelReader {
	
	@Autowired
    private ReceiptNumberGenerator receiptNumberGenerator;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private static final DecimalFormat NUMBER_FORMAT =
            new DecimalFormat("0");

    public List<DonationDto> read(MultipartFile file)
            throws Exception {

        List<DonationDto> donations = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                if (isRowEmpty(row)) {
                    continue;
                }

                DonationDto dto = new DonationDto();

                dto.setReceiptNo(receiptNumberGenerator.generateReceiptNo());
                dto.setName(getString(row.getCell(0)));
                dto.setMobile(getString(row.getCell(1)));
                dto.setAmount(parseAmount(row.getCell(2)));
                dto.setPaymentReceivedDate(getLocalDate(row.getCell(3)));
                dto.setDonationDate(getLocalDate(row.getCell(4)));
                dto.setPaymentMode(getString(row.getCell(5)));
                dto.setPurpose(getString(row.getCell(6)));
                dto.setRemarks(getString(row.getCell(7)));

                donations.add(dto);
            }

        }

        return donations;
    }

    //-----------------------------------------------------
    // LocalDate
    //-----------------------------------------------------
    
    private LocalDate getLocalDate(Cell cell) {

        if (cell == null) {
            return null;
        }

        try {

            if (cell.getCellType() == CellType.NUMERIC
                    && DateUtil.isCellDateFormatted(cell)) {

                return cell.getLocalDateTimeCellValue()
                           .toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {

                String value = cell.getStringCellValue().trim();

                if (value.isEmpty()) {
                    return null;
                }

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

                return LocalDate.parse(value, formatter);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }
    //-----------------------------------------------------
    // String
    //-----------------------------------------------------

    private String getString(Cell cell) {

        if (cell == null)
            return "";

        switch (cell.getCellType()) {

            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:

                if (DateUtil.isCellDateFormatted(cell)) {

                    return getDate(cell);

                }

                return NUMBER_FORMAT.format(
                        cell.getNumericCellValue());

            case BOOLEAN:
                return String.valueOf(
                        cell.getBooleanCellValue());

            case FORMULA:

                try {

                    return cell.getStringCellValue();

                } catch (Exception e) {

                    return NUMBER_FORMAT.format(
                            cell.getNumericCellValue());

                }

            default:
                return "";
        }

    }

    //-----------------------------------------------------
    // Amount
    //-----------------------------------------------------

    private Double parseAmount(Cell cell) {

        if (cell == null)
            return 0.0;

        switch (cell.getCellType()) {

            case NUMERIC:
                return cell.getNumericCellValue();

            case STRING:

                try {

                    return Double.parseDouble(
                            cell.getStringCellValue());

                } catch (Exception e) {

                    return 0.0;

                }

            default:
                return 0.0;
        }

    }

    //-----------------------------------------------------
    // Date
    //-----------------------------------------------------

    private String getDate(Cell cell) {

        if (cell == null)
            return "";

        try {

            if (DateUtil.isCellDateFormatted(cell)) {

                Date date = cell.getDateCellValue();

                LocalDate localDate =
                        date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                return DATE_FORMAT.format(localDate);

            }

            return getString(cell);

        } catch (Exception e) {

            return "";

        }

    }

    //-----------------------------------------------------
    // Empty Row
    //-----------------------------------------------------

    private boolean isRowEmpty(Row row) {

        for (Cell cell : row) {

            if (cell != null &&
                    cell.getCellType() != CellType.BLANK) {

                return false;

            }

        }

        return true;

    }

}