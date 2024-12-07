package com.group4.rankingmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "options")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "rankTitles")
@ToString(exclude = "rankTitles")
public class Option implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @Column(name = "score")
    @NotNull
    @Range(min = 1, max = 100)
    private Integer score;

    @Column(name = "explanation")
    @NotBlank
    @Length(min = 1)
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id",
            foreignKey = @ForeignKey(name = "fk_option_criteria"))
    @JsonBackReference(value = "criteriaOptions")
    private Criteria criteria;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "options",cascade = {CascadeType.MERGE })
    @JsonBackReference(value = "optionRankTitles")
    private Set<RankTitle> rankTitles;
}
