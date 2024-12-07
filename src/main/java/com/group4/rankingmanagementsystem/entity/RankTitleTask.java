package com.group4.rankingmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "rank_title_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankTitleTask implements Serializable{

    @EmbeddedId
    private RankTitleTaskId rankTitleTaskId;

    @Column(name = "in_working_hour")
    @NotNull
    @Range(min = 0, max = 99999999)
    private BigDecimal inWorkingHour;

    @Column(name = "overtime")
    @NotNull
    @Range(min = 0, max = 99999999)
    private BigDecimal overtime;
}

