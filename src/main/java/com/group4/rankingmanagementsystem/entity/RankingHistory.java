package com.group4.rankingmanagementsystem.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ranking_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ranking_decision")
    private String rankingDecision;

    @Column(name = "file_name")
    @NotBlank
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "uploaded_by")
    @NotBlank
    @Length(min = 1, max = 99)
    private String uploadedBy;

    @Column(name = "uploaded_at")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp uploadedAt;

    @Column(name = "status")
    @NotNull
    private Boolean status; //true: Success ,  false: Failed

    @Column(name = "note")
    @Nullable
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_ranking_history_group"))
    private Group group;

}
