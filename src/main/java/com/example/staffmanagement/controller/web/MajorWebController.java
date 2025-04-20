package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.MajorDTO;
import com.example.staffmanagement.service.MajorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/web/majors")
public class MajorWebController {

    private final MajorService majorService;

    @Autowired
    public MajorWebController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    public String getAllMajors(Model model) {
        model.addAttribute("title", "Danh sách ngành học");
        model.addAttribute("majors", majorService.getAllMajors());
        return "major/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("title", "Thêm ngành học mới");
        model.addAttribute("major", new MajorDTO());
        return "major/form";
    }

    @PostMapping("/add")
    public String addMajor(@Valid @ModelAttribute("major") MajorDTO majorDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm ngành học mới");
            return "major/form";
        }

        try {
            majorService.createMajor(majorDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm ngành học thành công");
            return "redirect:/web/majors";
        } catch (Exception e) {
            model.addAttribute("title", "Thêm ngành học mới");
            model.addAttribute("error", e.getMessage());
            return "major/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
            MajorDTO major = majorService.getMajorById(id);
            model.addAttribute("title", "Cập nhật thông tin ngành học");
            model.addAttribute("major", major);
            return "major/form";
        } catch (Exception e) {
            return "redirect:/web/majors";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateMajor(@PathVariable UUID id,
                              @Valid @ModelAttribute("major") MajorDTO majorDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Cập nhật thông tin ngành học");
            return "major/form";
        }

        try {
            majorService.updateMajor(id, majorDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin ngành học thành công");
            return "redirect:/web/majors";
        } catch (Exception e) {
            model.addAttribute("title", "Cập nhật thông tin ngành học");
            model.addAttribute("error", e.getMessage());
            return "major/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMajor(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            majorService.deleteMajor(id);
            redirectAttributes.addFlashAttribute("success", "Xóa ngành học thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/majors";
    }

    @GetMapping("/view/{id}")
    public String viewMajor(@PathVariable UUID id, Model model) {
        try {
            MajorDTO major = majorService.getMajorById(id);
            model.addAttribute("title", "Thông tin ngành học");
            model.addAttribute("major", major);
            return "major/view";
        } catch (Exception e) {
            return "redirect:/web/majors";
        }
    }
}