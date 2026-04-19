package com.petlife.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.petlife.model.BeautyRecordForm;
import com.petlife.model.Member;
import com.petlife.model.Pet;
import com.petlife.service.BeautyRecordService;
import com.petlife.service.MemberService;
import com.petlife.service.PetService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FrontBeautyBookingController {

    private final BeautyRecordService beautyRecordService;
    private final PetService petService;
    private final MemberService memberService;

    public FrontBeautyBookingController(BeautyRecordService beautyRecordService,
                                        PetService petService,
                                        MemberService memberService) {
        this.beautyRecordService = beautyRecordService;
        this.petService = petService;
        this.memberService = memberService;
    }

    @GetMapping("/beauty-booking")
    public String showBookingPage(Model model, HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/loginChoice";
        }

        Member member = memberService.findById(memberId);
        List<Pet> petList = petService.findActivePetsByMemberId(memberId);

        model.addAttribute("memberId", session.getAttribute("memberId"));
        model.addAttribute("memberName", session.getAttribute("memberName"));
        model.addAttribute("member", member);
        model.addAttribute("petList", petList);
        model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());
        model.addAttribute("form", new BeautyRecordForm());

        return "beautyBooking";
    }

    @PostMapping("/beauty-booking")
    public String createBooking(@ModelAttribute("form") BeautyRecordForm form,
                                Model model,
                                HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/loginChoice";
        }

        Member member = memberService.findById(memberId);
        List<Pet> petList = petService.findActivePetsByMemberId(memberId);

        model.addAttribute("memberId", session.getAttribute("memberId"));
        model.addAttribute("memberName", session.getAttribute("memberName"));
        model.addAttribute("member", member);
        model.addAttribute("petList", petList);
        model.addAttribute("beautyItems", beautyRecordService.getBeautyItemsForSelect());

        Pet pet = petService.findPet(form.getPetId());
        if (pet == null || pet.getMember() == null || !pet.getMember().getMemberId().equals(memberId)) {
            model.addAttribute("errorMessage", "只能為目前登入會員名下的寵物預約");
            return "beautyBooking";
        }
        
        try {
            form.setStatus("預約中");
            beautyRecordService.create(form);
            model.addAttribute("successMessage", "美容預約建立成功");
            model.addAttribute("form", new BeautyRecordForm());
            return "beautyBooking";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("form", form);
            return "beautyBooking";
        }
    }
}