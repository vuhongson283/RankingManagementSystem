package com.group4.rankingmanagementsystem.web.mvc;


import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.Option;
import com.group4.rankingmanagementsystem.service.CriteriaService;
import com.group4.rankingmanagementsystem.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/criteria-list")
public class CriteriaListController {
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    OptionService optionService;

    @GetMapping
    public String criteriaList(Model model) {
        List<Criteria> criteriaList = this.criteriaService.getAll();
        List<Object[]> objList = this.optionService.getListCriteria();

        model.addAttribute("criteriaList", criteriaList);
        model.addAttribute("objList", objList);
        return "Criteria/ListCriteria";
    }


    @PostMapping("/add-criteria")
    public String addCriteria2(Model model, Criteria criteria, RedirectAttributes redirectAttributes) {
        criteria.setName(criteria.getName().trim().replaceAll("\\s+", " ").replaceAll("[^a-zA-Z0-9\\- ]", ""));
        boolean isSuccess = criteriaService.createCriteria(criteria);
        if (isSuccess) {
            redirectAttributes.addFlashAttribute("message", "Add Successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Add Failed! This Criteria Name Alredy Exis, Plase Try Another Name!");
        }
        return "redirect:/criteria-list";
    }


    @PostMapping("/delete-criteria")
    public String deleteCriteria(@RequestParam("id") long id, RedirectAttributes redirectAttributes) {

        boolean isSuccess = criteriaService.deleteCriteria(id);
        if (isSuccess) {
            redirectAttributes.addFlashAttribute("message", "Delete Successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Delete Failed!Plase Try Again After Delete All Option In This Criteria!");
        }
        return "redirect:/criteria-list";
    }

    @GetMapping("/edit-criteria/{id}")
    public String editCriteria(@PathVariable("id") int id, Model model) {
        List<Option> optionList = this.optionService.getOptionsByCriteriaId(id);
        Option option = new Option();
        model.addAttribute("option", option);
        model.addAttribute("optionList", optionList);
        model.addAttribute("criteria", criteriaService.getCriteriaById(id));

        return "Criteria/testpage2";
    }

    @PostMapping("/edit-criteria/edit-name")
    public String editCriteriaName(@RequestParam("name") String name,
                                   @RequestParam("id") long id,
                                   RedirectAttributes redirectAttributes) {
        Criteria criteria = this.criteriaService.getCriteriaById(id);
        criteria.setName(name.trim().replaceAll("\\s+", " ").replaceAll("[^a-zA-Z0-9\\- ]", ""));
        boolean isSuccess = criteriaService.updateCriteria(criteria);
        if(isSuccess){
            redirectAttributes.addFlashAttribute("message", "Update Criteria Name Successfully!");
        } else{
            redirectAttributes.addFlashAttribute("message", "Update Failed, Please Try Again!");
        }
        return "redirect:/criteria-list/edit-criteria/" + id;
    }


}
