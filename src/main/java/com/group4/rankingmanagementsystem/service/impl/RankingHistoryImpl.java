package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.RankingHistory;
import com.group4.rankingmanagementsystem.repository.EmployeeRepository;
import com.group4.rankingmanagementsystem.repository.RankingHistoryRepository;
import com.group4.rankingmanagementsystem.service.RankingHistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingHistoryImpl implements RankingHistoryService {

    private RankingHistoryRepository rankingHistoryRepository;
    private EmployeeRepository employeeRepository;

    @Autowired
    public RankingHistoryImpl(RankingHistoryRepository rankingHistoryRepository, EmployeeRepository employeeRepository) {
        this.rankingHistoryRepository = rankingHistoryRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public List<RankingHistory> getAllRankingHistoryByGroupId(Long groupId) {
        return rankingHistoryRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<Object[]> getAllInforRankingHistoryByGroupId(Long groupId) {
        return employeeRepository.findAllRankingHistoryByGroupId(groupId);
    }


}
