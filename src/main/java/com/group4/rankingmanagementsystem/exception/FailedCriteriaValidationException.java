package com.group4.rankingmanagementsystem.exception;

import lombok.Getter;

@Getter
public class FailedCriteriaValidationException extends RuntimeException{

    public FailedCriteriaValidationException(String message) {
        super(message);
    }

}
