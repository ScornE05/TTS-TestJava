package com.example.staffmanagement.util;

import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.dto.ImportHistoryDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ExcelUtils {

    private static final String[] STAFF_HEADER = {
            "Mã nhân viên", "Họ và tên", "Email FE", "Email FPT", "Cơ sở", "Bộ môn", "Chuyên ngành", "Trạng thái"
    };

    public static ByteArrayInputStream staffToExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Nhân viên");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < STAFF_HEADER.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(STAFF_HEADER[col]);
                cell.setCellStyle(headerCellStyle);

                sheet.setColumnWidth(col, 6000);
            }

            for (int i = 0; i < 10; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < STAFF_HEADER.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(dataCellStyle);
                }
            }

            Sheet guideSheet = workbook.createSheet("Hướng dẫn");
            Row guideRow1 = guideSheet.createRow(0);
            guideRow1.createCell(0).setCellValue("Hướng dẫn import dữ liệu:");

            Row guideRow2 = guideSheet.createRow(1);
            guideRow2.createCell(0).setCellValue("1. Mã nhân viên: Không được trùng, tối đa 15 ký tự");

            Row guideRow3 = guideSheet.createRow(2);
            guideRow3.createCell(0).setCellValue("2. Họ và tên: Tối đa 100 ký tự");

            Row guideRow4 = guideSheet.createRow(3);
            guideRow4.createCell(0).setCellValue("3. Email FE: Phải có định dạng @fe.edu.vn, tối đa 100 ký tự");

            Row guideRow5 = guideSheet.createRow(4);
            guideRow5.createCell(0).setCellValue("4. Email FPT: Phải có định dạng @fpt.edu.vn, tối đa 100 ký tự");

            Row guideRow6 = guideSheet.createRow(5);
            guideRow6.createCell(0).setCellValue("5. Cơ sở: Nhập mã cơ sở");

            Row guideRow7 = guideSheet.createRow(6);
            guideRow7.createCell(0).setCellValue("6. Bộ môn: Nhập mã bộ môn");

            Row guideRow8 = guideSheet.createRow(7);
            guideRow8.createCell(0).setCellValue("7. Chuyên ngành: Nhập mã chuyên ngành");

            Row guideRow9 = guideSheet.createRow(8);
            guideRow9.createCell(0).setCellValue("8. Trạng thái: Nhập 1 (Hoạt động) hoặc 0 (Không hoạt động)");

            for (int i = 0; i < 9; i++) {
                guideSheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo Excel template: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream staffsToExcel(List<StaffDTO> staffs) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Nhân viên");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < STAFF_HEADER.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(STAFF_HEADER[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (StaffDTO staff : staffs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(staff.getStaffCode());
                row.createCell(1).setCellValue(staff.getName());
                row.createCell(2).setCellValue(staff.getAccountFe());
                row.createCell(3).setCellValue(staff.getAccountFpt());
                row.createCell(7).setCellValue(staff.getStatus() == 1 ? "Hoạt động" : "Không hoạt động");
            }

            for (int i = 0; i < STAFF_HEADER.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    public static List<StaffDTO> parseExcelFile(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<StaffDTO> staffs = new ArrayList<>();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (isRowEmpty(currentRow)) {
                    continue;
                }

                StaffDTO staff = new StaffDTO();
                staff.setId(UUID.randomUUID());

                Cell staffCodeCell = currentRow.getCell(0);
                if (staffCodeCell != null) {
                    staff.setStaffCode(getCellValueAsString(staffCodeCell));
                }

                Cell nameCell = currentRow.getCell(1);
                if (nameCell != null) {
                    staff.setName(getCellValueAsString(nameCell));
                }

                Cell feEmailCell = currentRow.getCell(2);
                if (feEmailCell != null) {
                    staff.setAccountFe(getCellValueAsString(feEmailCell));
                }

                Cell fptEmailCell = currentRow.getCell(3);
                if (fptEmailCell != null) {
                    staff.setAccountFpt(getCellValueAsString(fptEmailCell));
                }


                Cell statusCell = currentRow.getCell(7);
                if (statusCell != null) {
                    String statusValue = getCellValueAsString(statusCell);
                    staff.setStatus("1".equals(statusValue) ? (byte) 1 : (byte) 0);
                } else {
                    staff.setStatus((byte) 1); // Default là hoạt động
                }

                staffs.add(staff);
            }

            workbook.close();
            return staffs;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        if (row.getLastCellNum() <= 0) {
            return true;
        }

        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((int) value);
                    }
                    return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public static boolean isExcelFile(MultipartFile file) {
        return file != null &&
                (file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        file.getContentType().equals("application/vnd.ms-excel"));
    }
}