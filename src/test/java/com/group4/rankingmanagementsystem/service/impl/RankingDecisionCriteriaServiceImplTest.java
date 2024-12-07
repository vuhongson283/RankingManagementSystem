package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.exception.FailedCriteriaValidationException;
import com.group4.rankingmanagementsystem.exception.FinalizedRankingDecisionException;
import com.group4.rankingmanagementsystem.repository.CriteriaRepository;
import com.group4.rankingmanagementsystem.repository.OptionRepository;
import com.group4.rankingmanagementsystem.repository.RankingDecisionCriteriaRepository;
import com.group4.rankingmanagementsystem.service.CriteriaService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Modify Criteria Config")
public class RankingDecisionCriteriaServiceImplTest {

    @InjectMocks
    RankingDecisionCriteriaServiceImpl rankingDecisionCriteriaService;

    @Mock
    RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository;

    @Mock
    RankingDecisionService rankingDecisionService;

    @Mock
    CriteriaRepository criteriaRepository;

    @Mock
    OptionRepository optionRepository;

    @Mock
    CriteriaService criteriaService;

    static RankingDecision finalizedRankingDecision;
    static RankingDecision validRankingDecision;
    @BeforeAll
    static void setup(){
        finalizedRankingDecision = new RankingDecision();
        finalizedRankingDecision.setId(2L);
        finalizedRankingDecision.setStatus(true);

        validRankingDecision = new RankingDecision();
        validRankingDecision.setId(1L);
        validRankingDecision.setStatus(false);
    }

    @DisplayName("When Ranking Decision does not exist")
    @Test
    @Order(1)
    void testModifyCriteriaConfig_whenRankingDecisionDoesNotExist_thenThrowsRuntimeException(){
        //Arrange
        Long rankingDecisionId = 5L;
        Long[] criteriaToRemove = new Long[]{3L};
        Long[] criteriaToAdd = new Long[]{4L};
        String[] weightsToChange = new String[]{"1-30", "2-50", "4-20"};
        Mockito.when(rankingDecisionService.findOne(rankingDecisionId)).thenReturn(null);

        //Act
        //Assert
        assertThrows(EntityNotFoundException.class,
                ()->rankingDecisionCriteriaService.modifyCriteriaConfig(
                        rankingDecisionId,criteriaToRemove,criteriaToAdd,weightsToChange),"Test1 Failed");
    }

    @DisplayName("When Ranking Decision was finalized")
    @Test
    @Order(2)
    void testModifyCriteriaConfig_whenRankingDecisionWasFinalized_thenThrowsIllegalArgumentException(){
        //Arrange
        Long rankingDecisionId = 2L;
        Long[] criteriaToRemove = new Long[]{3L};
        Long[] criteriaToAdd = new Long[]{4L};
        String[] weightsToChange = new String[]{"1-30", "2-50", "4-20"};
        Mockito.when(rankingDecisionService.findOne(rankingDecisionId)).thenReturn(finalizedRankingDecision);

        //Act
        //Assert
        assertThrows(FinalizedRankingDecisionException.class,
                ()->rankingDecisionCriteriaService.modifyCriteriaConfig(
                        rankingDecisionId,criteriaToRemove,criteriaToAdd,weightsToChange),"Test2 Failed");
    }

    @DisplayName("When total weight of criteria is not 100")
    @Test
    @Order(3)
    void testModifyCriteriaConfig_whenTotalWeightIsNot100_thenThrowsRuntimeException(){
        //Arrange
        Long rankingDecisionId = 1L;
        Long[] criteriaToRemove = new Long[]{3L};
        Long[] criteriaToAdd = new Long[]{4L};
        String[] weightsToChange = new String[]{"1-30", "2-50", "4-100"};
        Mockito.when(rankingDecisionService.findOne(rankingDecisionId)).thenReturn(validRankingDecision);

        //Act
        //Assert
        assertThrows(FailedCriteriaValidationException.class,
                ()->rankingDecisionCriteriaService.modifyCriteriaConfig(
                        rankingDecisionId,criteriaToRemove,criteriaToAdd,weightsToChange),"Test3 Failed");
    }

    @DisplayName("When criteria does not exist")
    @Test
    @Order(4)
    void testModifyCriteriaConfig_whenCriteriaDoesNotExist_thenThrowsRuntimeException(){
        //Arrange
        Long rankingDecisionId = 1L;
        Long[] criteriaToRemove = new Long[]{0L};
        Long[] criteriaToAdd = new Long[]{4L};
        String[] weightsToChange = new String[]{"1-30", "2-50", "4-20"};
        Mockito.when(rankingDecisionService.findOne(rankingDecisionId)).thenReturn(validRankingDecision);
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class,
                ()->rankingDecisionCriteriaService.modifyCriteriaConfig(
                        rankingDecisionId,criteriaToRemove,criteriaToAdd,weightsToChange),"Test4 Failed");
    }

}
