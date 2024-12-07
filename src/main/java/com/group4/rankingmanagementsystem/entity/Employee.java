package com.group4.rankingmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    @Length(min = 1, max = 99)
    private String name;

    @Column(name = "username", unique = true)
    @NotBlank
    @Length(min = 1, max = 99)
    private String username;

    @Column(name = "email", unique = true)
    @Email
    @Length(min = 1, max = 99)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_group"))
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id",
            foreignKey = @ForeignKey(name = "fk_employee_rank"))
    private RankTitle rankTitle;

}
