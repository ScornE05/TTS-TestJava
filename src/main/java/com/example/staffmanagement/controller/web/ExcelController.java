package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.ImportHistoryDTO;
import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.service.*;
import com.example.staffmanagement.util.ExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/web/excel")
public class ExcelController {

    private final StaffService staffService;
    private final FacilityService facilityService;
    private final DepartmentService departmentService;
    private final MajorService majorService;
    private final ImportHistoryService importHistoryService;

    @Autowired
    public ExcelController(
            StaffService staffService,
            FacilityService facilityService,
            DepartmentService departmentService,
            MajorService majorService,
            ImportHistoryService importHistoryService) {
        this.staffService = staffService;
        this.facilityService = facilityService;
        this.departmentService = departmentService;
        this.majorService = majorService;
        this.importHistoryService = importHistoryService;
    }

    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=staff_template.xlsx");

        ByteArrayInputStream stream = ExcelUtils.staffToExcelTemplate();
        org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
    }

    @GetMapping("/export-staffs")
    public void exportStaffs(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=staffs.xlsx");

        List<StaffDTO> staffs = staffService.getAllStaffs();
        ByteArrayInputStream stream = ExcelUtils.staffsToExcel(staffs);
        org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
    }

    @GetMapping("/import-form")
    public String showImportForm(Model model) {
        model.addAttribute("title", "Import nhân viên");
        return "staff/import";
    }

    @PostMapping("/import-staffs")
    public String importStaffs(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        // Kiểm tra file
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file để import");
            return "redirect:/web/excel/import-form";
        }

        // Kiểm tra định dạng file
        if (!ExcelUtils.isExcelFile(file)) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file Excel (.xlsx, .xls)");
            return "redirect:/web/excel/import-form";
        }

        try {
            // Đọc dữ liệu từ file Excel
            List<StaffDTO> staffs = ExcelUtils.parseExcelFile(file.getInputStream());

            // Ghi lại thông tin import
            ImportHistoryDTO importHistory = new ImportHistoryDTO();
            importHistory.setId(UUID.randomUUID());
            importHistory.setFileName(file.getOriginalFilename());
            importHistory.setTotalRecords(staffs.size());
            importHistory.setImportedBy("Admin"); // Thay bằng thông tin người dùng đăng nhập

            // Biến lưu kết quả import
            List<String> successDetails = new ArrayList<>();
            List<String> failureDetails = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            // Thực hiện import từng nhân viên
            for (int i = 0; i < staffs.size(); i++) {
                StaffDTO staff = staffs.get(i);
                try {
                    // Thực hiện validate và lưu nhân viên
                    staffService.createStaff(staff);

                    // Lưu kết quả thành công
                    successCount++;
                    successDetails.add("Dòng " + (i + 2) + ": Import thành công nhân viên " + staff.getStaffCode() + " - " + staff.getName());
                } catch (Exception e) {
                    // Lưu kết quả thất bại
                    failCount++;
                    failureDetails.add("Dòng " + (i + 2) + ": Lỗi - " + e.getMessage());
                }
            }

            // Cập nhật thông tin import
            importHistory.setSuccessRecords(successCount);
            importHistory.setFailedRecords(failCount);
            importHistory.setSuccessDetails(String.join("\n", successDetails));
            importHistory.setFailureDetails(String.join("\n", failureDetails));

            // Lưu lịch sử import
            importHistoryService.createImportHistory(importHistory);

            // Thông báo kết quả
            redirectAttributes.addFlashAttribute("success",
                    "Import hoàn tất: " + successCount + " thành công, " + failCount + " thất bại");

            return "redirect:/web/import-histories";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đọc file: " + e.getMessage());
            return "redirect:/web/excel/import-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/web/excel/import-form";
        }
    }

    @GetMapping("/import-histories")
    public String showImportHistories(Model model) {
        List<ImportHistoryDTO> histories = importHistoryService.getAllImportHistories();
        model.addAttribute("histories", histories);
        model.addAttribute("title", "Lịch sử import");
        return "staff/import-histories";
    }

    @GetMapping("/import-history/{id}")
    public String viewImportHistory(@PathVariable UUID id, Model model) {
        ImportHistoryDTO history = importHistoryService.getImportHistoryById(id);
        model.addAttribute("history", history);
        model.addAttribute("title", "Chi tiết lịch sử import");
        return "staff/import-history-detail";
    }
}