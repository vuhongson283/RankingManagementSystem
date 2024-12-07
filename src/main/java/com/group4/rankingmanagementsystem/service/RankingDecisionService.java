package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RankingDecisionService {
    RankingDecision findOne(Long id);

    Set<RankingDecisionCriteria> getCriteriaSetOf(Long id);

    int renameRankingDecision(Long id, String name);

    RankingDecision findRankingDecisionbyID(Long id);

    void finalizeRankingDecision(Long id);

    void remove(Long id);

    boolean isRankingDecisionNameExists(String name, Long groupID);

    void addNewRankingDecision(RankingDecision rankingDecision);

    List<RankingDecision> listRankDecisionNotGroupID(Long groupID);

    RankingDecision cloneRankingDecision(Long sourceRankingDecisionId, Long targetGroupId);
}

