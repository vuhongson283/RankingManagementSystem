package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.RankingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingHistoryRepository extends JpaRepository<RankingHistory, Long> {
    List<RankingHistory> findAllByGroupId(Long groupId);






}
