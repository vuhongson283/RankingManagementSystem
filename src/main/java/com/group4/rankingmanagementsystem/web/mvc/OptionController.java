package com.group4.rankingmanagementsystem.web.mvc;

import com.group4.rankingmanagementsystem.entity.Option;
import com.group4.rankingmanagementsystem.service.CriteriaService;
import com.group4.rankingmanagementsystem.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/option-list")
public class OptionController {
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    OptionService optionService;

    @PostMapping("/add-or-update")
    public String addOption(@RequestParam("name") String name,
                            @RequestParam("score") Integer score,
                            @RequestParam("explanation") String explanation,
                            @RequestParam("criteria_id") Long criteria_id, Model model,
                            @RequestParam("id") long id,
                            RedirectAttributes redirectAttributes) {
        Option option = new Option();
        option.setName(name.trim().replaceAll("\\s+", " ").replaceAll("[^a-zA-Z0-9\\- ]", ""));
        option.setScore(score);
        option.setExplanation(explanation.trim().replaceAll("\\s+", " "));
        option.setCriteria(criteriaService.getCriteriaById(criteria_id));
        if(id==-1){
            boolean isSuccess = optionService.addOption(option);
            if (isSuccess) {
                redirectAttributes.addFlashAttribute("message", "Add Successfully!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Add Failed! Please Try Again!!");
            }
        } else {
            option.setId(id);
            boolean isSuccess = optionService.updateOption(option);
            if (isSuccess) {
                redirectAttributes.addFlashAttribute("message", "Update Successfully!");
            } else {
                redirectAttributes.addFlashAttribute("message", "Update Failed! This Option Name Alredy Exis, Plase Try Another Name!");
            }
        }
        return "redirect:/criteria-list/edit-criteria/" + criteria_id;
    }

    @PostMapping("/delete-option")
    public String deleteOption(@RequestParam("id") long id,
                               @RequestParam("criteria_id") long criteria_id,
                               Model model, RedirectAttributes redirectAttributes){
        Option option = optionService.getOptionById(id);
        boolean isSuccess = optionService.deleteOption(option);
        if (isSuccess) {
            redirectAttributes.addFlashAttribute("message", "Delete Option Successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Delete Failed! Please Try Again!!");
        }
        return "redirect:/criteria-list/edit-criteria/" + criteria_id;
    }



}
