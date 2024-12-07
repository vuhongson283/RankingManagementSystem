package com.group4.rankingmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "rank_title")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude={"employees","rankTitleTasks"})
@ToString(exclude={"employees","rankTitleTasks"})
public class RankTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ranking_decision_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rank_title_ranking_decision"))
    private RankingDecision rankingDecision;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankTitle")
    private Set<Employee> employees;

    @ManyToMany(fetch = FetchType.LAZY,  cascade = {CascadeType.MERGE, CascadeType.REMOVE })
    @JoinTable(name = "rank_title_option",
            joinColumns = { @JoinColumn(name = "rank_title_id")},
            inverseJoinColumns = {@JoinColumn(name = "option_id")})
    private Set<Option> options;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankTitleTaskId.rankTitle", cascade = CascadeType.ALL)
    private Set<RankTitleTask> rankTitleTasks;
}
