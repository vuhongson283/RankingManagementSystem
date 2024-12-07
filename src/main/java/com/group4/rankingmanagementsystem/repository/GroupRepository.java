package com.group4.rankingmanagementsystem.repository;
import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{
    Group getGroupById(Long id);

    @Query("SELECT " +
            "    g.id AS GroupID, " +
            "    g.name AS GroupName, " +
            "    COUNT(e.id) AS NumberOfEmployee, " +
            "    rd.name AS CurrentRankingName " +
            "FROM " +
            "    Group g " +
            "LEFT JOIN " +
            "    g.employees e " + // sử dụng tên thuộc tính của thực thể Group
            "LEFT JOIN " +
            "    g.currentRankingDecision rd " + // sử dụng tên thuộc tính của thực thể Group
            "GROUP BY " +
            "    g.id, g.name, rd.name")
    List<Object[]> findGroupWithRankingName();

    @Query("SELECT g.id,g.name AS groupName, rd.name AS rankingDecisionName " +
            "FROM Group g " +
            "LEFT JOIN g.currentRankingDecision rd " +
            "WHERE g.id = :id")
    Object findGroupNameAndRankingDecisionNameById(@Param("id") Long id);

    @Query("SElECT rd from RankingDecision rd where rd.group.id=:groupID")
    List<RankingDecision> findAllRankdecisionbyGID(@Param("groupID") Long groupID);

    @Query("SELECT g.name, rd.name FROM Group g JOIN g.rankingDecisions rd WHERE rd.status = true AND g.id = :groupID")
    List<Object[]> findGroupAndRankingDecisionNamesByGroupIdAndStatus(
            @Param("groupID") Long groupID);


    @Query("SELECT rd FROM RankingDecision rd WHERE rd.group.id = :groupID AND rd.status = true")
    List<RankingDecision> findAllRankingNamesByGroupIdAndStatus(@Param("groupID") Long groupID);


    boolean existsByName(String name);

}
