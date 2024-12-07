package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import com.group4.rankingmanagementsystem.repository.CriteriaRepository;
import com.group4.rankingmanagementsystem.service.CriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CriteriaServiceImpl implements CriteriaService {
    private final CriteriaRepository criteriaRepository;

    @Autowired
    public CriteriaServiceImpl(CriteriaRepository criteriaRepository) {
        this.criteriaRepository = criteriaRepository;
    }

    @Override
    public Criteria fineOne(Long id) {
        Optional<Criteria> criteria = criteriaRepository.findById(id);
        return criteria.isPresent() ? criteria.get() : null;
    }

    @Override
    public Set<Criteria> findCriteriaExcept(Set<RankingDecisionCriteria> currentCriteria) {
        if(currentCriteria.isEmpty()){
            return new HashSet<>(criteriaRepository.findAll());
        }

        List<Long> ids = new ArrayList<>();

        for (RankingDecisionCriteria e: currentCriteria
             ) {
            ids.add(e.getRankingDecisionCriteriaId().getCriteria().getId());
        }

        return criteriaRepository.findByIdNotIn(ids);
    }

    @Override
    public List<Criteria> getAll() {
        return this.criteriaRepository.findAll();
    }

    @Override
    public Criteria getCriteriaById(long id) {
        return this.criteriaRepository.getReferenceById(id);
    }

    @Override
    public Boolean createCriteria(Criteria criteria) {
        try{

            criteriaRepository.save(criteria);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public Boolean updateCriteria(Criteria criteria) {
        try {
            this.criteriaRepository.save(criteria);
            return true;
        }
        catch(Exception e){
            return false;
        }

    }

    @Override
    public Boolean deleteCriteria(long id) {
        try{
            Criteria criteria = criteriaRepository.getReferenceById(id);
            criteriaRepository.delete(criteria);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
