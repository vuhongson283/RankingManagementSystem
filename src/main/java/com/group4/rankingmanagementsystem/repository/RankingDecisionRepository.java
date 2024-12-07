package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingDecisionRepository extends JpaRepository<RankingDecision, Long>,
        PagingAndSortingRepository<RankingDecision, Long>{

    @Modifying
    @Query("update RankingDecision rd set rd.name = :name where rd.id = :id")
    @Transactional
    int setNameForRankingDecision(@Param("name") String name, @Param("id") Long id);

    List<RankingDecision> findByNameAndGroup(String name, Group group);
    boolean existsByName(String name);

    List<RankingDecision> findByNameAndGroupId(String name, Long groupId);
    List<RankingDecision> findByGroup_IdNot(Long groupId);

    boolean existsByNameAndGroupId(String name, Long groupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Override
    <S extends RankingDecision> S save(S entity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT rd FROM RankingDecision rd WHERE rd.id = :id")
    Optional<RankingDecision> findWithLockingById(@Param("id") Long id);
}
