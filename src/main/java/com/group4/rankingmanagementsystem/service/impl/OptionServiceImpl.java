package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.Option;
import com.group4.rankingmanagementsystem.repository.CriteriaRepository;
import com.group4.rankingmanagementsystem.repository.OptionRepository;
import com.group4.rankingmanagementsystem.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OptionServiceImpl implements OptionService {
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private CriteriaRepository criteriaRepository;


    @Override
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    @Override
    public List<Option> getOptionsByCriteriaId(long criteriaId) {
        Criteria criteria = this.criteriaRepository.getReferenceById(criteriaId);
        return this.optionRepository.findByCriteria(criteria);
    }

    @Override
    public Option getOptionById(long optionId) {
        Option option = this.optionRepository.getReferenceById(optionId);
        return option;
    }

    @Override
    public boolean addOption(Option option) {
        try {
            optionRepository.save(option);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateOption(Option option) {
        try {
            optionRepository.save(option);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteOption(Option option) {
        try {
            optionRepository.delete(option);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getMaxScrore(Criteria criteria) {
        int maxScore = this.optionRepository.getMaxScoreByCriteria(criteria);
        return maxScore;
    }

    @Override
    public int getNumberOfOption(Criteria criteria) {
        int num = this.optionRepository.getNumberOfOptionByCriteria(criteria);
        return num;
    }

    @Override
    public List<Object[]> getListCriteria() {
        return this.optionRepository.findCriteriaList();
    }
}
