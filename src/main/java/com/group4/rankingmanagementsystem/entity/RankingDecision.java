package com.group4.rankingmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "ranking_decision")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude={"rankingDecisionCriterias", "rankTitles"})
@ToString(exclude={"rankingDecisionCriterias", "rankTitles"})
public class RankingDecision implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @Column(name = "status")
    @NotNull
    private Boolean status; //true: Finalized ,  false: Draft

    @Column(name = "finalized_by")
    @Nullable
    @Length(min = 1, max = 99)
    private String finalizedBy;

    @Column(name = "finalized_at")
    @Nullable
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp finalizedAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ranking_decision_group"))
    @JsonBackReference(value = "groupRankingDecision")
    private Group group;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankingDecisionCriteriaId.rankingDecision", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rankingDecisionCriteriaId ASC")
    @JsonManagedReference
    private Set<RankingDecisionCriteria> rankingDecisionCriterias;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankingDecision", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RankTitle> rankTitles;
}
