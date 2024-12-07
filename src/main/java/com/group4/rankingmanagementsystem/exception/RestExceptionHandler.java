package com.group4.rankingmanagementsystem.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({ EntityNotFoundException.class, FailedCriteriaValidationException.class,
            UnacceptableOptionException.class, DuplicatedRankTitleNameException.class, EmptyDataTableException.class})
    public ResponseEntity<String> handleEntityNotFoundException(Exception e, HttpServletRequest request) {
        String message = "Error occurred updating ";
        String uri = request.getRequestURI();
        if(uri.contains("criteria-config")){
            message += "Criteria";
        }else if(uri.contains("rank-title-config")){
            message += "Rank Title";
        }else if(uri.contains("task-n-price-config")){
            message += "Task & Price";
        }
        message += " Configuration. Please try again.";
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({ FinalizedRankingDecisionException.class})
    public ResponseEntity<String> handleFinalizedRankingDecisionException(Exception e, HttpServletRequest request){
        if(request.getMethod().equalsIgnoreCase("DELETE")){
            ResponseEntity.badRequest().body("Error occurred removing Ranking Decision. Please try again.");
        }
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(){
        return ResponseEntity.badRequest().body("Error occur. Please try again!");
    }
}
