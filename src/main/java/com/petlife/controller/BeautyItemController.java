package com.petlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.BeautyItemForm;
import com.petlife.service.BeautyItemService;

@Controller
@RequestMapping("/admin/beauty/item")
public class BeautyItemController {

    private final BeautyItemService beautyItemService;

    public BeautyItemController(BeautyItemService beautyItemService) {
        this.beautyItemService = beautyItemService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "all") String queryType,
                       @RequestParam(defaultValue = "") String queryValue,
                       Model model) {
        try {
            model.addAttribute("itemList", beautyItemService.findForList(queryType, queryValue));
        } catch (Exception e) {
            model.addAttribute("itemList", java.util.List.of());
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("queryType", queryType);
        model.addAttribute("queryValue", queryValue);
        return "beautyItemList";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("form", new BeautyItemForm());
        return "beautyItemAdd";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("form") BeautyItemForm form, Model model) {
        try {
            beautyItemService.create(form);
            return "redirect:/admin/beauty/item/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "beautyItemAdd";
        }
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("form", beautyItemService.getFormById(id));
        return "beautyItemEdit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("form") BeautyItemForm form, Model model) {
        try {
            beautyItemService.update(form);
            return "redirect:/admin/beauty/item/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "beautyItemEdit";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        beautyItemService.delete(id);
        return "redirect:/admin/beauty/item/list";
    }
}