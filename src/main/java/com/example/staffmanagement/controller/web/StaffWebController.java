package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/web/staffs")
public class StaffWebController {

    private final StaffService staffService;

    @Autowired
    public StaffWebController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String getAllStaffs(Model model) {
        model.addAttribute("title", "Danh sách nhân viên");
        model.addAttribute("staffs", staffService.getAllStaffs());
        return "staff/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("title", "Thêm nhân viên mới");
        model.addAttribute("staff", new StaffDTO());
        return "staff/form";
    }

    @PostMapping("/add")
    public String addStaff(@Valid @ModelAttribute("staff") StaffDTO staffDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm nhân viên mới");
            return "staff/form";
        }

        try {
            staffService.createStaff(staffDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công");
            return "redirect:/web/staffs";
        } catch (Exception e) {
            model.addAttribute("title", "Thêm nhân viên mới");
            model.addAttribute("error", e.getMessage());
            return "staff/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
            StaffDTO staff = staffService.getStaffById(id);
            model.addAttribute("title", "Cập nhật thông tin nhân viên");
            model.addAttribute("staff", staff);
            return "staff/form";
        } catch (Exception e) {
            return "redirect:/web/staffs";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateStaff(@PathVariable UUID id,
                              @Valid @ModelAttribute("staff") StaffDTO staffDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Cập nhật thông tin nhân viên");
            return "staff/form";
        }

        try {
            staffService.updateStaff(id, staffDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin nhân viên thành công");
            return "redirect:/web/staffs";
        } catch (Exception e) {
            model.addAttribute("title", "Cập nhật thông tin nhân viên");
            model.addAttribute("error", e.getMessage());
            return "staff/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/staffs";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleStaffStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            StaffDTO staff = staffService.toggleStaffStatus(id);
            String status = staff.getStatus() == 1 ? "hoạt động" : "không hoạt động";
            redirectAttributes.addFlashAttribute("success", "Đã đổi trạng thái nhân viên thành " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/staffs";
    }

    @GetMapping("/view/{id}")
    public String viewStaff(@PathVariable UUID id, Model model) {
        try {
            StaffDTO staff = staffService.getStaffById(id);
            model.addAttribute("title", "Thông tin nhân viên");
            model.addAttribute("staff", staff);
            return "staff/view";
        } catch (Exception e) {
            return "redirect:/web/staffs";
        }
    }
}