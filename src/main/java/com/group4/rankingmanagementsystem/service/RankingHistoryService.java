package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.RankingHistory;

import java.util.List;


public interface RankingHistoryService {
    public List<RankingHistory> getAllRankingHistoryByGroupId(Long groupId);
    public List<Object[]> getAllInforRankingHistoryByGroupId(Long groupId);
}
