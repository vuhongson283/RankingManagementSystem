package com.group4.rankingmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "`groups`")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude={"currentRankingDecision", "rankingDecisions","employees",
        "rankingHistories","rankingHistories", "tasks"})
@ToString(exclude={"currentRankingDecision", "rankingDecisions","employees",
        "rankingHistories","rankingHistories", "tasks"})
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_ranking_decision_id",
            foreignKey = @ForeignKey(name = "fk_group_current_ranking_decision"))
    private RankingDecision currentRankingDecision;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "groupRankingDecision")
    private Set<RankingDecision> rankingDecisions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<Employee> employees;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<RankingHistory> rankingHistories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.ALL)
    private Set<Task> tasks;
}
