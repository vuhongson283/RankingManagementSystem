package com.group4.rankingmanagementsystem.web.mvc;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.service.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/ranking-decision-management")
public class RankingDecisionManagementController {
    private final RankingDecisionService rankingDecisionService;
    private final CriteriaService criteriaService;
    private final RankingDecisionCriteriaService rankingDecisionCriteriaService;
    private final RankTitleService rankTitleService;
    private final RankTitleTaskService rankTitleTaskService;
    private final TaskService taskService;

    @Autowired
    public RankingDecisionManagementController(RankingDecisionService rankingDecisionService,
                                               CriteriaService criteriaService,
                                               RankingDecisionCriteriaService rankingDecisionCriteriaService,
                                               RankTitleService rankTitleService,
                                               RankTitleTaskService rankTitleTaskService,
                                               TaskService taskService) {
        this.rankingDecisionService = rankingDecisionService;
        this.criteriaService = criteriaService;
        this.rankingDecisionCriteriaService = rankingDecisionCriteriaService;
        this.rankTitleService = rankTitleService;
        this.rankTitleTaskService = rankTitleTaskService;
        this.taskService = taskService;
    }

    @GetMapping("/criteria-config/{id}")
    public String getCriteriaConfig(@PathVariable("id") Long rankingDecisionId, Model model){
        // common attribute
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        Set<RankingDecisionCriteria> rankingDecisionCriterias = rankingDecisionService.getCriteriaSetOf(rankingDecisionId);
        List<RankTitle> rankTitles = rankTitleService.findByRankingDecision(rankingDecision);
        List<RankTitleTask> rankTitleTasks = rankTitleTaskService.findByRankingDecision(rankingDecisionId);

        //criteria list but doesn't contain any criteria in rankingDecisionCriteria, so user choose to add more
        Set<Criteria> theRestOfCriteriaList = criteriaService.findCriteriaExcept(rankingDecisionCriterias);

        model.addAttribute("rankTitles", rankTitles);
        model.addAttribute("rankTitleTasks", rankTitleTasks);

        model.addAttribute("rankingDecision", rankingDecision);
        model.addAttribute("rankingDecisionCriterias", rankingDecisionCriterias);
        model.addAttribute("theRestOfCriteriaList", theRestOfCriteriaList);

        return "ranking-decision-management/criteria-config";
    }

    @GetMapping("/rank-title-config/{id}")
    public String getRankTitleConfig(@PathVariable("id") Long rankingDecisionId, Model model){
        // common attribute
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        Set<RankingDecisionCriteria> rankingDecisionCriterias = rankingDecisionService.getCriteriaSetOf(rankingDecisionId);
        List<RankTitle> rankTitles = rankTitleService.findByRankingDecision(rankingDecision);
        List<RankTitleTask> rankTitleTasks = rankTitleTaskService.findByRankingDecision(rankingDecisionId);

        //rank-title-config attribute
        //nạp tất cả option trong rank title
        for (RankTitle rankTitle : rankTitles) {
            Hibernate.initialize(rankTitle.getOptions());
        }

        model.addAttribute("rankTitles", rankTitles);
        model.addAttribute("rankTitleTasks", rankTitleTasks);

        model.addAttribute("rankingDecision", rankingDecision);
        model.addAttribute("rankingDecisionCriterias", rankingDecisionCriterias);

        if(rankingDecisionCriterias == null || rankingDecisionCriterias.isEmpty()){
            return "redirect:/ranking-decision-management/criteria-config/"+rankingDecisionId;
        }

        return "ranking-decision-management/ranktitle-config";
    }

    @PostMapping("/add-criteria")
    public String addCriteria(@RequestParam Long rankingDecisionId, @RequestParam Long criteriaId){
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        Criteria criteria = criteriaService.fineOne(criteriaId);

        RankingDecisionCriteria rankingDecisionCriteria = new RankingDecisionCriteria(
                new RankingDecisionCriteriaId(rankingDecision,criteria),
                0
        );
        rankingDecisionCriteriaService.insertOrUpdate(rankingDecisionCriteria);

        return "redirect:/ranking-decision-management/criteria-config/"+rankingDecisionId;
    }

    @GetMapping("/task-n-price-config/{id}")
    public String getTaskAndPriceConfig(@PathVariable("id") Long rankingDecisionId, Model model){
        // common attribute
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        Set<RankingDecisionCriteria> rankingDecisionCriterias = rankingDecisionService.getCriteriaSetOf(rankingDecisionId);
        List<RankTitle> rankTitles = rankTitleService.findByRankingDecision(rankingDecision);
        List<RankTitleTask> rankTitleTasks = rankTitleTaskService.findByRankingDecision(rankingDecisionId);
        List<Task> tasks = rankTitleTaskService.getTasksByRankingDecision(rankingDecisionId);
        List<Task> theRestOfTaskList = taskService.findTasksExcept(tasks);

        model.addAttribute("rankTitles", rankTitles);
        model.addAttribute("rankTitleTasks", rankTitleTasks);
        model.addAttribute("tasks", tasks);
        model.addAttribute("theRestOfTaskList", theRestOfTaskList);
        model.addAttribute("rankingDecision", rankingDecision);
        model.addAttribute("rankingDecisionCriterias", rankingDecisionCriterias);

        if(rankingDecisionCriterias == null || rankingDecisionCriterias.isEmpty()){
            return "redirect:/ranking-decision-management/criteria-config/"+rankingDecisionId;
        }
        if(rankTitles == null || rankTitles.isEmpty()){
            return "redirect:/ranking-decision-management/rank-title-config/"+rankingDecisionId;
        }

        return "ranking-decision-management/tasknprice-config";
    }
}
