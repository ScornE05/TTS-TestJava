package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.DepartmentDTO;
import com.example.staffmanagement.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/web/departments")
public class DepartmentWebController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentWebController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String getAllDepartments(Model model) {
        model.addAttribute("title", "Danh sách phòng ban");
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "department/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("title", "Thêm phòng ban mới");
        model.addAttribute("department", new DepartmentDTO());
        return "department/form";
    }

    @PostMapping("/add")
    public String addDepartment(@Valid @ModelAttribute("department") DepartmentDTO departmentDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm phòng ban mới");
            return "department/form";
        }

        try {
            departmentService.createDepartment(departmentDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng ban thành công");
            return "redirect:/web/departments";
        } catch (Exception e) {
            model.addAttribute("title", "Thêm phòng ban mới");
            model.addAttribute("error", e.getMessage());
            return "department/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
            DepartmentDTO department = departmentService.getDepartmentById(id);
            model.addAttribute("title", "Cập nhật thông tin phòng ban");
            model.addAttribute("department", department);
            return "department/form";
        } catch (Exception e) {
            return "redirect:/web/departments";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(@PathVariable UUID id,
                                   @Valid @ModelAttribute("department") DepartmentDTO departmentDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Cập nhật thông tin phòng ban");
            return "department/form";
        }

        try {
            departmentService.updateDepartment(id, departmentDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin phòng ban thành công");
            return "redirect:/web/departments";
        } catch (Exception e) {
            model.addAttribute("title", "Cập nhật thông tin phòng ban");
            model.addAttribute("error", e.getMessage());
            return "department/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng ban thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/departments";
    }

    @GetMapping("/view/{id}")
    public String viewDepartment(@PathVariable UUID id, Model model) {
        try {
            DepartmentDTO department = departmentService.getDepartmentById(id);
            model.addAttribute("title", "Thông tin phòng ban");
            model.addAttribute("department", department);
            return "department/view";
        } catch (Exception e) {
            return "redirect:/web/departments";
        }
    }
}