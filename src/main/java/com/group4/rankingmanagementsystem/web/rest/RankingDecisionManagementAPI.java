package com.group4.rankingmanagementsystem.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.rankingmanagementsystem.entity.RankTitle;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import com.group4.rankingmanagementsystem.service.RankTitleService;
import com.group4.rankingmanagementsystem.service.RankTitleTaskService;
import com.group4.rankingmanagementsystem.service.RankingDecisionCriteriaService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/ranking-decision-management-api")
public class RankingDecisionManagementAPI {

    private final RankingDecisionCriteriaService rankingDecisionCriteriaService;
    private final RankTitleService rankTitleService;
    private final RankingDecisionService rankingDecisionService;
    private final RankTitleTaskService rankTitleTaskService;

    @Autowired
    public RankingDecisionManagementAPI(RankingDecisionCriteriaService rankingDecisionCriteriaService,
                                        RankTitleService rankTitleService,
                                        RankingDecisionService rankingDecisionService,
                                        RankTitleTaskService rankTitleTaskService) {
        this.rankingDecisionCriteriaService = rankingDecisionCriteriaService;
        this.rankTitleService = rankTitleService;
        this.rankingDecisionService = rankingDecisionService;
        this.rankTitleTaskService = rankTitleTaskService;
    }

    @PostMapping("/criteria-config/{id}")
    public ResponseEntity<Object> modifyCriteriaConfig(@PathVariable("id") Long rankingDecisionId,
                                      @RequestParam(required = false) Long[] criteriaToRemove,
                                      @RequestParam(required = false) Long[] criteriaToAdd,
                                      @RequestParam(required = false) String[] weightsToChange) {

        rankingDecisionCriteriaService.modifyCriteriaConfig(rankingDecisionId, criteriaToRemove,
                    criteriaToAdd, weightsToChange);

        return ResponseEntity.ok().body("Criteria Configuration successfully updated.");
    }

    @GetMapping("/rank-title-config/{id}")
    public ResponseEntity<Object> checkDuplicatedRankTitle(@PathVariable("id") Long rankingDecisionId,
                                               @RequestParam String rankTitleName){
        try {
            RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
            if(rankTitleService.isExistedByNameAndRankingDecision(rankTitleName, rankingDecision)){
                return new ResponseEntity<>("Rank Title existed!", HttpStatus.ACCEPTED);
            }else{
                Set<RankingDecisionCriteria> rankingDecisionCriteriaSet =
                        rankingDecisionService.getCriteriaSetOf(rankingDecisionId); // dùng để lấy ra các option
                return new ResponseEntity<>(rankingDecisionCriteriaSet, HttpStatus.OK);
            }

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/rank-title-config/{id}")
    public ResponseEntity<Object> modifyRankTitleConfig(@PathVariable("id") Long rankingDecisionId,
                                                        @RequestBody Map<String,Object> payload){
        Map<String,List<String>> rankTitlesToUpdate = (Map<String, List<String>>) payload.get("rankTitlesToUpdate");
        List<String> rankTitlesToRemove = (List<String>) payload.get("rankTitlesToRemove");

        rankTitleService.modifyRankTitleConfig(rankingDecisionId, rankTitlesToRemove, rankTitlesToUpdate);

        return ResponseEntity.ok().body("Title Configuration successfully updated.");
    }

    @PutMapping("/task-n-price-config/{id}")
    public ResponseEntity<Object> modifyTaskAndPriceConfig(@PathVariable("id") Long rankingDecisionId,
                                                        @RequestBody Map<String,Object> payload){
        Map<String,String> inWorkingHourToUpdate = (Map<String, String>) payload.get("inWorkingHourToUpdate");
        Map<String,String> overtimeToUpdate = (Map<String, String>) payload.get("overtimeToUpdate");
        List<String> tasksToRemove = (List<String>) payload.get("tasksToRemove");
        List<String> tasksToAdd = (List<String>) payload.get("tasksToAdd");
        rankTitleTaskService.modifyTaskAndPriceConfig(rankingDecisionId, tasksToRemove, tasksToAdd,
                    inWorkingHourToUpdate, overtimeToUpdate);
        return ResponseEntity.ok().body("Task & Price Configuration successfully updated.");
    }
}
