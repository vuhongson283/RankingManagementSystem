package com.group4.rankingmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "criteria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude={"rankingDecisionCriterias", "options"})
@ToString(exclude={"rankingDecisionCriterias", "options"})
public class Criteria implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "criteria", cascade = CascadeType.ALL)
    @OrderBy("score DESC")
    @JsonManagedReference(value = "criteriaOptions")
    private Set<Option> options;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankingDecisionCriteriaId.criteria", cascade = CascadeType.ALL)
//    @OrderBy("rankingDecisionCriteriaId.criteria.id ASC")
    @JsonBackReference
    private Set<RankingDecisionCriteria> rankingDecisionCriterias;
}
