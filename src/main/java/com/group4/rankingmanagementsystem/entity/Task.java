package com.group4.rankingmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "task")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "rankTitleTasks")
@ToString(exclude = "rankTitleTasks")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rankTitleTaskId.task", cascade = CascadeType.ALL)
    private Set<RankTitleTask> rankTitleTasks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_group"))
    private Group group;
}
