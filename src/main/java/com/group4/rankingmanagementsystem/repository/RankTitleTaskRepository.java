package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.RankTitleTask;
import com.group4.rankingmanagementsystem.entity.RankTitleTaskId;
import com.group4.rankingmanagementsystem.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankTitleTaskRepository extends JpaRepository<RankTitleTask, RankTitleTaskId>{
    @Query("select rtt from RankTitle rt join rt.rankTitleTasks rtt" +
            "                           join rtt.rankTitleTaskId.task t" +
            " where rt.rankingDecision.id = :rankingDecisionId")
    List<RankTitleTask> findByRankingDecision(@Param("rankingDecisionId") Long rankingDecisionId);

    @Query("select t from RankTitle rt join rt.rankTitleTasks rtt" +
            "                           join rtt.rankTitleTaskId.task t" +
            " where rt.rankingDecision.id = :rankingDecisionId" +
            " group by t.id")
    List<Task> getTasksByRankingDecision(@Param("rankingDecisionId") Long rankingDecisionId);
}
