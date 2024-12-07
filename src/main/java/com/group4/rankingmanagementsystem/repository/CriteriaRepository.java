package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long>,
        PagingAndSortingRepository<Criteria, Long>{
    Set<Criteria> findByIdNotIn(Collection<Long> ids);
    Optional<Criteria> findByName(String name);
}
