package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Criteria;
import com.group4.rankingmanagementsystem.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>,
        PagingAndSortingRepository<Option, Long>{
    @Query("SELECT o FROM Option o WHERE o.criteria = :criteria")
    List<Option> findByCriteria(@Param("criteria") Criteria criteria);
    @Query("SELECT MAX(o.score) FROM Option o WHERE o.criteria = :criteria")
    Integer getMaxScoreByCriteria(@Param("criteria") Criteria criteria);
    @Query("SELECT COUNT(o) FROM Option o WHERE o.criteria = :criteria")
    int getNumberOfOptionByCriteria(@Param("criteria") Criteria criteria);
    @Query("SELECT \n" +
            "    c.id, \n" +
            "    COUNT(o.id) AS numberOfOptionsForEachCriteria, \n" +
            "    c.name\n" +
            "FROM \n" +
            "    Criteria c\n" +
            "LEFT JOIN \n" +
            "    Option o ON c.id = o.criteria.id\n" +
            "GROUP BY \n" +
            "    c.id, \n" +
            "    c.name")
    List<Object[]> findCriteriaList();

    @Modifying
    @Query(value = "DELETE FROM rank_title_option WHERE option_id = :optionId", nativeQuery = true)
    void deleteRankTitleOptionByOptionId(@Param("optionId") Long rankTitleId);

    Optional<Option> findByNameAndCriteria(String name, Criteria criteria);
}
