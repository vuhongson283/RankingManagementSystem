package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankTitle;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankTitleRepository extends JpaRepository<RankTitle, Long>,
        PagingAndSortingRepository<RankTitle, Long> {

    List<RankTitle> findByRankingDecision(RankingDecision rankingDecision);
    List<RankTitle> findByNameAndRankingDecision(String name, RankingDecision rankingDecision);
    boolean existsByNameAndRankingDecision(String name, RankingDecision rankingDecision);
}
