package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;

public interface RankingDecisionCriteriaService {
    RankingDecisionCriteria insertOrUpdate(RankingDecisionCriteria rankingDecisionCriteria);
    void modifyCriteriaConfig(Long rankingDecisionId, Long[] criteriaToRemove,
                              Long[] criteriaToAdd, String[] weightsToChange);
}
