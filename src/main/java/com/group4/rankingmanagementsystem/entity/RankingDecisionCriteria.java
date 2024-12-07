package com.group4.rankingmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Entity
@Table(name = "ranking_decision_criteria")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingDecisionCriteria implements Serializable{

    @Id
    private RankingDecisionCriteriaId rankingDecisionCriteriaId;

    @Column(name = "weight")
    @NotNull
    @Range(min = 0, max = 100)
    private Integer weight;
}
