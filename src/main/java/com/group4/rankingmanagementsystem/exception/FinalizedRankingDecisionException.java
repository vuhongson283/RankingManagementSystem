package com.group4.rankingmanagementsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class FinalizedRankingDecisionException extends RuntimeException{

    public FinalizedRankingDecisionException(String message) {
        super(message);
    }
}
