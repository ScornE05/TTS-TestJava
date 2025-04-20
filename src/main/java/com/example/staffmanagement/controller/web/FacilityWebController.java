package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.FacilityDTO;
import com.example.staffmanagement.service.FacilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/web/facilities")
public class FacilityWebController {

    private final FacilityService facilityService;

    @Autowired
    public FacilityWebController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public String getAllFacilities(Model model) {
        model.addAttribute("title", "Danh sách cơ sở");
        model.addAttribute("facilities", facilityService.getAllFacilities());
        return "facility/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("title", "Thêm cơ sở mới");
        model.addAttribute("facility", new FacilityDTO());
        return "facility/form";
    }

    @PostMapping("/add")
    public String addFacility(@Valid @ModelAttribute("facility") FacilityDTO facilityDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm cơ sở mới");
            return "facility/form";
        }

        try {
            facilityService.createFacility(facilityDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm cơ sở thành công");
            return "redirect:/web/facilities";
        } catch (Exception e) {
            model.addAttribute("title", "Thêm cơ sở mới");
            model.addAttribute("error", e.getMessage());
            return "facility/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        try {
            FacilityDTO facility = facilityService.getFacilityById(id);
            model.addAttribute("title", "Cập nhật thông tin cơ sở");
            model.addAttribute("facility", facility);
            return "facility/form";
        } catch (Exception e) {
            return "redirect:/web/facilities";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateFacility(@PathVariable UUID id,
                                 @Valid @ModelAttribute("facility") FacilityDTO facilityDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Cập nhật thông tin cơ sở");
            return "facility/form";
        }

        try {
            facilityService.updateFacility(id, facilityDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin cơ sở thành công");
            return "redirect:/web/facilities";
        } catch (Exception e) {
            model.addAttribute("title", "Cập nhật thông tin cơ sở");
            model.addAttribute("error", e.getMessage());
            return "facility/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteFacility(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            facilityService.deleteFacility(id);
            redirectAttributes.addFlashAttribute("success", "Xóa cơ sở thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/facilities";
    }

    @GetMapping("/view/{id}")
    public String viewFacility(@PathVariable UUID id, Model model) {
        try {
            FacilityDTO facility = facilityService.getFacilityById(id);
            model.addAttribute("title", "Thông tin cơ sở");
            model.addAttribute("facility", facility);
            return "facility/view";
        } catch (Exception e) {
            return "redirect:/web/facilities";
        }
    }
}