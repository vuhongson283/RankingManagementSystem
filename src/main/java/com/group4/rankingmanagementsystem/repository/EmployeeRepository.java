package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Employee;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    @Query("SELECT " +
            "e.id, " +
            "e.name, " +
            "g.name, " +
            "rd.name, " +
            "rt.name " +
            "FROM Employee e " +
            "JOIN e.group g " +
            "JOIN e.rankTitle rt " +
            "JOIN g.currentRankingDecision rd " +
            "WHERE g.id = :groupId")
    List<Object[]> findAllRankingHistoryByGroupId(@Param("groupId") Long groupId);

    @Query("update Employee e Set e.rankTitle = null where e.rankTitle.rankingDecision = :rankingDecision")
    @Modifying
    void updateNullRankTitleByRankingDecision(@Param("rankingDecision")RankingDecision rankingDecision);
}
