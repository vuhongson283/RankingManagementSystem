package com.group4.rankingmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RankingDecisionCriteriaId implements Serializable{

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ranking_criteria_id")
    @JsonBackReference
    private RankingDecision rankingDecision;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "criteria_id")
    @JsonManagedReference
    private Criteria criteria;
}
