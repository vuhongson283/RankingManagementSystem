package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.exception.FinalizedRankingDecisionException;
import com.group4.rankingmanagementsystem.repository.EmployeeRepository;
import com.group4.rankingmanagementsystem.repository.RankTitleRepository;
import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteriaId;
import com.group4.rankingmanagementsystem.repository.RankingDecisionCriteriaRepository;
import com.group4.rankingmanagementsystem.repository.RankingDecisionRepository;
import com.group4.rankingmanagementsystem.service.RankTitleService;
import com.group4.rankingmanagementsystem.service.RankTitleTaskService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RankingDecisionServiceImpl implements RankingDecisionService {
    private final RankingDecisionRepository rankingDecisionRepository;
    private final RankTitleRepository rankTitleRepository;
    private final RankTitleTaskService rankTitleTaskService;
    private final RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public RankingDecisionServiceImpl(RankingDecisionRepository rankingDecisionRepository,
                                      RankTitleRepository rankTitleRepository,
                                      RankTitleTaskService rankTitleTaskService,
                                      RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository,
                                      EmployeeRepository employeeRepository) {
        this.rankingDecisionRepository = rankingDecisionRepository;
        this.rankTitleRepository = rankTitleRepository;
        this.rankTitleTaskService = rankTitleTaskService;
        this.rankingDecisionCriteriaRepository = rankingDecisionCriteriaRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public RankingDecision findOne(Long id) {
        RankingDecision rankingDecision = rankingDecisionRepository.findById(id)
                .orElseThrow(() ->new EntityNotFoundException("Ranking Decision not found."));
        return rankingDecision;
    }

    @Override
    public Set<RankingDecisionCriteria> getCriteriaSetOf(Long id) {
        RankingDecision rankingDecision = findOne(id);
        if(rankingDecision != null){
            return rankingDecision.getRankingDecisionCriterias();
        }
        return null;
    }

    @Override
    @Transactional
    public int renameRankingDecision(Long id, String name) {
        RankingDecision rankingDecision = findOne(id);

        if(rankingDecision == null){
            return -1;
        }else if(rankingDecision.getStatus()){
            return -2;
        }

        List<RankingDecision> rankingDecisionsWithSameName = rankingDecisionRepository
                .findByNameAndGroup(name, rankingDecision.getGroup());

        if(!rankingDecisionsWithSameName.isEmpty()){
            return 0;
        }

        return rankingDecisionRepository.setNameForRankingDecision(name, id);
    }

    @Override
    public RankingDecision findRankingDecisionbyID(Long id) {
        return rankingDecisionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + id));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, isolation = Isolation.SERIALIZABLE)
    public void finalizeRankingDecision(Long id){
        RankingDecision rankingDecision = rankingDecisionRepository.findWithLockingById(id)
                .orElseThrow(()->new EntityNotFoundException("Ranking Decision not found."));
        if(rankingDecision.getStatus()){
            throw new FinalizedRankingDecisionException("Ranking Decision was finalized.");
        }

        Set<RankingDecisionCriteria> rankingDecisionCriterias = getCriteriaSetOf(id);
        List<RankTitle> rankTitles = rankTitleRepository.findByRankingDecision(rankingDecision);
        List<RankTitleTask> rankTitleTasks = rankTitleTaskService.findByRankingDecision(id);

        if(!rankingDecisionCriterias.isEmpty() && !rankTitles.isEmpty() && !rankTitleTasks.isEmpty()){
            rankingDecision.setStatus(true);
            rankingDecision.setFinalizedAt(new Timestamp(System.currentTimeMillis()));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
                UserDetails user = (UserDetails) authentication.getPrincipal();
                rankingDecision.setFinalizedBy(user.getUsername());
            }
        }else{
            throw new FinalizedRankingDecisionException("Cannot finalize Ranking Decision lacking of data");
        }
        rankingDecisionRepository.save(rankingDecision);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void remove(Long id) {
        RankingDecision rankingDecision = rankingDecisionRepository
                .findById(id).orElseThrow(()-> new EntityNotFoundException("Ranking Decision not found."));

        if(rankingDecision.getStatus()){ //nếu ranking decision đó đã finalized thì ko dc xóa.
            throw new FinalizedRankingDecisionException("Cannot delete finalized Ranking Decision.");
        }
        employeeRepository.updateNullRankTitleByRankingDecision(rankingDecision);
        rankingDecisionRepository.delete(rankingDecision);
    }

    public boolean isRankingDecisionNameExists(String name, Long groupID) {
        List<RankingDecision> existingDecisions = rankingDecisionRepository.findByNameAndGroupId(name, groupID);
        return !existingDecisions.isEmpty();
    }


    @Override
    public void addNewRankingDecision(RankingDecision rankingDecision) {
        rankingDecisionRepository.save(rankingDecision);
    }

    @Override
    public List<RankingDecision> listRankDecisionNotGroupID(Long groupID) {
        return rankingDecisionRepository.findByGroup_IdNot(groupID);
    }

    @Override
    public RankingDecision cloneRankingDecision(Long sourceRankingDecisionId, Long targetGroupId) {
        RankingDecision sourceRankingDecision = rankingDecisionRepository.findById(sourceRankingDecisionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ranking decision ID"));

        // Kiểm tra nếu tên của quyết định xếp hạng đã tồn tại trong nhóm đích
        if (rankingDecisionRepository.existsByNameAndGroupId(sourceRankingDecision.getName(), targetGroupId)) {
            throw new IllegalArgumentException("Ranking decision name already exists in the target group");
        }

        // Tạo một bản sao mới của RankingDecision
        RankingDecision clonedRankingDecision = new RankingDecision();
        clonedRankingDecision.setName(sourceRankingDecision.getName());
        clonedRankingDecision.setStatus(sourceRankingDecision.getStatus());
        clonedRankingDecision.setFinalizedBy(sourceRankingDecision.getFinalizedBy());
        clonedRankingDecision.setFinalizedAt(sourceRankingDecision.getFinalizedAt());
        clonedRankingDecision.setGroup(new Group());
        clonedRankingDecision.getGroup().setId(targetGroupId);

        // Lưu clonedRankingDecision vào repository
        RankingDecision savedClonedRankingDecision = rankingDecisionRepository.save(clonedRankingDecision);

        // Sao chép các RankingDecisionCriteria liên quan
        Set<RankingDecisionCriteria> sourceCriteria = sourceRankingDecision.getRankingDecisionCriterias();
        Set<RankingDecisionCriteria> clonedCriteria = sourceCriteria.stream().map(criteria -> {
            RankingDecisionCriteria newCriteria = new RankingDecisionCriteria();
            newCriteria.setRankingDecisionCriteriaId(new RankingDecisionCriteriaId(savedClonedRankingDecision, criteria.getRankingDecisionCriteriaId().getCriteria()));
            newCriteria.setWeight(criteria.getWeight());
            return newCriteria;
        }).collect(Collectors.toSet());

        // Lưu các clonedCriteria vào repository
        rankingDecisionCriteriaRepository.saveAll(clonedCriteria);

        return savedClonedRankingDecision;
    }
}
