package com.group4.rankingmanagementsystem.web.mvc;


import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.service.GroupService;

import org.springframework.ui.Model;
import com.group4.rankingmanagementsystem.service.RankingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("bulk-ranking-history")
public class BulkRankingController {

    private RankingHistoryService rankingHistoryService;
    private GroupService groupService;

    @Autowired
    public BulkRankingController(RankingHistoryService rankingHistoryService , GroupService groupService) {
        this.rankingHistoryService = rankingHistoryService;
        this.groupService = groupService;
    }

    @GetMapping
    public String listRankingHistory(Model model , @RequestParam("id") Long id) {
        model.addAttribute("rankingHistoryList", rankingHistoryService.getAllRankingHistoryByGroupId(id));
        Object groupInfoation = groupService.findGroupbyID(id);
        List<RankingDecision> currentRankList = groupService.listRankingDecisionbyGroupID(id);
        model.addAttribute("RankDecisionList",currentRankList);
        if (groupInfoation != null) {
            Object[] obj = (Object[]) groupInfoation;
            model.addAttribute("groupID", obj[0]);
            model.addAttribute("groupName", obj[1]);
            model.addAttribute("rankingDecisionName", obj[2]);
        }
        List<Object[]> rankingHistory = rankingHistoryService.getAllInforRankingHistoryByGroupId(id);
        model.addAttribute("rankingHistory", rankingHistory);
        if (rankingHistory != null && !rankingHistory.isEmpty()) {
            for (Object[] obj : rankingHistory) {
                model.addAttribute("employeeID", obj[0]);
                model.addAttribute("employeeName", obj[1]);
                model.addAttribute("rankingGroupName", obj[2]);
                model.addAttribute("currentRankingDecisionName", obj[3]);
                model.addAttribute("currentRank", obj[4]);
            }
        }
        return "ranking-history/bulk-ranking-history-list";
    }








}
