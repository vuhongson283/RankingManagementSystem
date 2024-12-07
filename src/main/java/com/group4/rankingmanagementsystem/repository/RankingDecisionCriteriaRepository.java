package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteriaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingDecisionCriteriaRepository extends JpaRepository<RankingDecisionCriteria, RankingDecisionCriteriaId>,
        PagingAndSortingRepository<RankingDecisionCriteria, RankingDecisionCriteriaId>{
}
