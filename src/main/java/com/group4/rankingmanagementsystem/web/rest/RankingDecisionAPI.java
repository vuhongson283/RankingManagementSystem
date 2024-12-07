package com.group4.rankingmanagementsystem.web.rest;

import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import com.group4.rankingmanagementsystem.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ranking-decision-api")
public class RankingDecisionAPI {
    private final RankingDecisionService rankingDecisionService;

    @Autowired
    public RankingDecisionAPI(RankingDecisionService rankingDecisionService) {
        this.rankingDecisionService = rankingDecisionService;
    }

    @PutMapping("/rename/{id}")
    public ResponseEntity<Object> renameRankingDecision(@PathVariable("id") Long id,
                                                        @RequestParam() String name){
        if(name.isBlank()){
            return new ResponseEntity<>("Ranking Decision name is invalid!", HttpStatus.FOUND);
        }
        name = StringUtil.removeExtraSpaces(name);

        int rowsAffected = rankingDecisionService.renameRankingDecision(id, name);
        if(rowsAffected == -1){
            return new ResponseEntity<>("Error! Please try again", HttpStatus.NOT_FOUND);
        }else if(rowsAffected == -2){
            return new ResponseEntity<>("Cannot rename finalized Ranking Decision", HttpStatus.BAD_REQUEST);
        }else if(rowsAffected == 0){
            return new ResponseEntity<>("Ranking Decision name is duplicated!", HttpStatus.FOUND);
        }else{
            return new ResponseEntity<>(name, HttpStatus.OK);
        }
    }

    @PutMapping("/finalize/{id}")
    public ResponseEntity<Object> finalizeRankingDecision(@PathVariable("id") Long id){
        rankingDecisionService.finalizeRankingDecision(id);
        return ResponseEntity.ok().body("Finalized successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeRankingDecision(@PathVariable("id") Long id){
        rankingDecisionService.remove(id);
        return ResponseEntity.ok().body("Ranking Decision successfully removed.");
    }
}
