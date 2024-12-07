package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.Option;

import java.util.List;

public interface OptionService {
    List<Option> getAllOptions();
    List<Option> getOptionsByCriteriaId(long criteriaId);
    Option getOptionById(long optionId);
    boolean addOption(Option option);
    boolean updateOption(Option option);
    boolean deleteOption(Option option);
    int getMaxScrore(Criteria criteria);
    int getNumberOfOption(Criteria criteria);
    List<Object[]> getListCriteria();

}
