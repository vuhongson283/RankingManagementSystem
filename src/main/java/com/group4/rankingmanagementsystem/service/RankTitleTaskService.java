package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.RankTitleTask;
import com.group4.rankingmanagementsystem.entity.Task;

import java.util.List;
import java.util.Map;

public interface RankTitleTaskService {

    List<RankTitleTask> findByRankingDecision(Long rankingDecisionId);
    List<Task> getTasksByRankingDecision(Long rankingDecisionId);
    void modifyTaskAndPriceConfig(Long rankingDecisionId, List<String> tasksToRemove, List<String> tasksToAdd,
                                  Map<String,String> inWorkingHourToUpdate, Map<String,String> overtimeToUpdate);
}
