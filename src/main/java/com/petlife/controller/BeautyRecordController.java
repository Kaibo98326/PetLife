package com.petlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.BeautyRecordForm;
import com.petlife.service.BeautyRecordService;

@Controller
@RequestMapping("/admin/beauty/record")
public class BeautyRecordController {

    private final BeautyRecordService beautyRecordService;

    public BeautyRecordController(BeautyRecordService beautyRecordService) {
        this.beautyRecordService = beautyRecordService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Integer searchPetId,
                       @RequestParam(required = false) Integer searchBeautyId,
                       @RequestParam(required = false) String searchStatus,
                       Model model) {
        model.addAttribute("recordList", beautyRecordService.search(searchPetId, searchBeautyId, searchStatus));
        model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
        model.addAttribute("searchPetId", searchPetId);
        model.addAttribute("searchBeautyId", searchBeautyId);
        model.addAttribute("searchStatus", searchStatus);
        return "beautyRecordList";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("form", new BeautyRecordForm());
        model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
        return "beautyRecordAdd";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("form") BeautyRecordForm form, Model model) {
        try {
            beautyRecordService.create(form);
            return "redirect:/admin/beauty/record/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
            return "beautyRecordAdd";
        }
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("form", beautyRecordService.getFormById(id));
        model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
        return "beautyRecordEdit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("form") BeautyRecordForm form, Model model) {
        try {
            beautyRecordService.update(form);
            return "redirect:/admin/beauty/record/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
            return "beautyRecordEdit";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        beautyRecordService.delete(id);
        return "redirect:/admin/beauty/record/list";
    }
}