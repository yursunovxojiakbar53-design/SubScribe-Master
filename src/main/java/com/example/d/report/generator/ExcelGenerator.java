package com.example.d.report.generator;

import com.example.d.report.dto.SubscriptionReportRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class ExcelGenerator {

    private static final String[] HEADERS = {
            "Obuna nomi", "Oylik narx", "Valyuta",
            "Asosiy valyutadagi ekvivalenti", "Asosiy valyuta",
            "Oxirgi 1 oylik haqiqiy xarajat", "Oxirgi 1 yillik haqiqiy xarajat"
    };

    public byte[] generate(List<SubscriptionReportRow> rows) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Xarajatlar hisobot");

            writeHeader(workbook, sheet);
            writeRows(sheet, rows);
            writeTotalRow(sheet, rows);
            autoSizeColumns(sheet);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new UncheckedIOException("Excel hisobot yaratishda xatolik", e);
        }
    }

    private void writeHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeRows(Sheet sheet, List<SubscriptionReportRow> rows) {
        int rowIndex = 1;
        for (SubscriptionReportRow row : rows) {
            Row excelRow = sheet.createRow(rowIndex++);
            excelRow.createCell(0).setCellValue(row.serviceName());
            excelRow.createCell(1).setCellValue(row.monthlyPrice().doubleValue());
            excelRow.createCell(2).setCellValue(row.currency().name());
            excelRow.createCell(3).setCellValue(row.equivalentInBaseCurrency().doubleValue());
            excelRow.createCell(4).setCellValue(row.baseCurrency().name());
            excelRow.createCell(5).setCellValue(row.monthlyTotalInBaseCurrency().doubleValue());
            excelRow.createCell(6).setCellValue(row.yearlyTotalInBaseCurrency().doubleValue());
        }
    }


    private void writeTotalRow(Sheet sheet, List<SubscriptionReportRow> rows) {
        BigDecimal grandTotal = rows.stream()
                .map(SubscriptionReportRow::yearlyTotalInBaseCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Row totalRow = sheet.createRow(rows.size() + 2);
        totalRow.createCell(0).setCellValue("JAMI (oxirgi 1 yillik haqiqiy)");
        totalRow.createCell(6).setCellValue(grandTotal.doubleValue());
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}