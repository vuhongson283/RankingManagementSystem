package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.exception.FailedCriteriaValidationException;
import com.group4.rankingmanagementsystem.exception.FinalizedRankingDecisionException;
import com.group4.rankingmanagementsystem.repository.CriteriaRepository;
import com.group4.rankingmanagementsystem.repository.OptionRepository;
import com.group4.rankingmanagementsystem.repository.RankingDecisionCriteriaRepository;
import com.group4.rankingmanagementsystem.repository.RankingDecisionRepository;
import com.group4.rankingmanagementsystem.service.CriteriaService;
import com.group4.rankingmanagementsystem.service.RankingDecisionCriteriaService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingDecisionCriteriaServiceImpl implements RankingDecisionCriteriaService {

    private final RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository;
    private final RankingDecisionService rankingDecisionService;
    private final CriteriaRepository criteriaRepository;
    private final OptionRepository optionRepository;
    private final CriteriaService criteriaService;

    @Autowired
    public RankingDecisionCriteriaServiceImpl(RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository,
                                              RankingDecisionService rankingDecisionService,
                                              CriteriaRepository criteriaRepository,
                                              OptionRepository optionRepository,
                                              CriteriaService criteriaService) {
        this.rankingDecisionCriteriaRepository = rankingDecisionCriteriaRepository;
        this.rankingDecisionService = rankingDecisionService;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
        this.criteriaService = criteriaService;
    }

    @Override
    public RankingDecisionCriteria insertOrUpdate(RankingDecisionCriteria rankingDecisionCriteria) {

        return rankingDecisionCriteriaRepository.save(rankingDecisionCriteria);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, isolation = Isolation.SERIALIZABLE)
    public void modifyCriteriaConfig(Long rankingDecisionId, Long[] criteriaToRemove,
                                     Long[] criteriaToAdd, String[] weightsToChange){
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        if(rankingDecision == null){
            throw new EntityNotFoundException("Ranking Decision doesn't exist!");
        }else if(rankingDecision.getStatus()){
            throw new FinalizedRankingDecisionException("Cannot edit finalized Ranking Decision.");
        }

        List<Long> toRemove = criteriaToRemove == null ? new ArrayList<>() : Arrays.asList(criteriaToRemove);
        List<Long> toAdd = criteriaToAdd == null ? new ArrayList<>() : Arrays.asList(criteriaToAdd);

        //remove all repeated element if exist
        toRemove = toRemove.stream().distinct().collect(Collectors.toList());
        toAdd = toAdd.stream().distinct().collect(Collectors.toList());

        //list những weight của những criteria cần thay đổi (format: criteriaId-weight)
        List<String> toChange = Arrays.asList(weightsToChange);

        //kiểm tra tổng Weight của những record có id trong toChange có bằng 100 và khác 0 ko
        // (ngoại trừ những id nằm trong toRemove)
        boolean isZero = false;
        int sum =0;

        for (String e : toChange
             ) {
            String[] criteriaIdAndWeight = e.split("-");
            Long criteriaId = Long.parseLong(criteriaIdAndWeight[0]);
            int weight = Integer.parseInt(criteriaIdAndWeight[1]);
            if(weight == 0 && !toRemove.contains(criteriaId)){
                isZero = true;
                break;
            }
            if(!toRemove.contains(criteriaId)){
                sum += weight;
            }
        }

        //validate ko thành công
        if(sum != 100 || isZero){
            throw new FailedCriteriaValidationException("Failed validation!");
        }

        //add criteria
        for (Long criteriaId: toAdd
        ) {
            //nếu criteriaId ko tồn tại -> return false
            Criteria criteria = criteriaRepository.findById(criteriaId)
                    .orElseThrow(()->new EntityNotFoundException("Criteria not found."));
            if (!toRemove.contains(criteriaId)) {
                RankingDecisionCriteria rankingDecisionCriteria = new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision, criteria),
                        0
                );
                rankingDecisionCriteriaRepository.save(rankingDecisionCriteria);
            }
        }


        //thực lưu data đã được cập nhật vào db

        for (String e : toChange
        ) {
            String[] criteriaIdAndWeight = e.split("-");
            Long criteriaId = Long.parseLong(criteriaIdAndWeight[0]);
            int weight = Integer.parseInt(criteriaIdAndWeight[1]);
            if(weight != 0){
                //nếu criteriaId ko tồn tại -> return false
                Criteria criteria = criteriaRepository.findById(criteriaId)
                        .orElseThrow(()->new EntityNotFoundException("Criteria not found."));

                RankingDecisionCriteria rankingDecisionCriteria = new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision, criteria),
                        weight
                );
                rankingDecisionCriteriaRepository.save(rankingDecisionCriteria);
            }
        }

        //xóa những criteria trong list to Remove
        for (Long criteriaId: toRemove
             ) {
            //nếu criteriaId ko tồn tại -> throw exception
            Criteria criteria = criteriaRepository.findById(criteriaId)
                    .orElseThrow(()->new EntityNotFoundException("Criteria not found."));
            List<Option> newOptList = new ArrayList<>();

            //xóa các liên kết option-ranktitle
            for (Option opt: criteria.getOptions()
                 ) {
                optionRepository.deleteRankTitleOptionByOptionId(opt.getId());
            }
            rankingDecisionCriteriaRepository.deleteById(new RankingDecisionCriteriaId(rankingDecision, criteria));
        }
    }
}
