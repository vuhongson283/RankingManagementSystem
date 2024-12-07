package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import java.util.Set;
import java.util.List;

public interface CriteriaService {
    Criteria fineOne(Long id);
    Set<Criteria> findCriteriaExcept(Set<RankingDecisionCriteria> currentCriteria);
    List<Criteria> getAll();
    Criteria getCriteriaById(long id);
    Boolean createCriteria(Criteria criteria);
    Boolean updateCriteria(Criteria criteria);
    Boolean deleteCriteria(long id);

}
