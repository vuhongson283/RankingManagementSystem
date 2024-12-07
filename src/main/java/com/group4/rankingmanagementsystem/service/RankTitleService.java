package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.RankTitle;
import com.group4.rankingmanagementsystem.entity.RankingDecision;

import java.util.List;
import java.util.Map;

public interface RankTitleService {

    List<RankTitle> findAll();
    List<RankTitle> findByRankingDecision(RankingDecision rankingDecision);
    RankTitle insert(String rankTitleName, Long rankingDecisionId);
    boolean isExistedByNameAndRankingDecision(String name, RankingDecision rankingDecision);
    void modifyRankTitleConfig(Long rankingDecisionId, List<String> rankTitlesToRemove, Map<String,List<String>> rankTitlesToUpdate);
    double calculateRankScore(RankingDecision rankingDecision, RankTitle rankTitle);
}
