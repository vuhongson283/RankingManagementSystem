package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.exception.DuplicatedRankTitleNameException;
import com.group4.rankingmanagementsystem.exception.EmptyDataTableException;
import com.group4.rankingmanagementsystem.exception.FinalizedRankingDecisionException;
import com.group4.rankingmanagementsystem.exception.UnacceptableOptionException;
import com.group4.rankingmanagementsystem.repository.OptionRepository;
import com.group4.rankingmanagementsystem.repository.RankTitleRepository;
import com.group4.rankingmanagementsystem.repository.RankTitleTaskRepository;
import com.group4.rankingmanagementsystem.service.RankTitleService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import com.group4.rankingmanagementsystem.util.StringUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankTitleServiceImpl implements RankTitleService {

    private final RankTitleRepository rankTitleRepository;
    private final RankingDecisionService rankingDecisionService;
    private final OptionRepository optionRepository;
    private final RankTitleTaskRepository rankTitleTaskRepository;


    @Autowired
    public RankTitleServiceImpl(RankTitleRepository rankTitleRepository, RankingDecisionService rankingDecisionService,
                                OptionRepository optionRepository, RankTitleTaskRepository rankTitleTaskRepository) {
        this.rankTitleRepository = rankTitleRepository;
        this.rankingDecisionService = rankingDecisionService;
        this.optionRepository = optionRepository;
        this.rankTitleTaskRepository = rankTitleTaskRepository;
    }

    @Override
    public List<RankTitle> findAll() {
        return rankTitleRepository.findAll();
    }

    @Override
    public List<RankTitle> findByRankingDecision(RankingDecision rankingDecision) {
        return rankTitleRepository.findByRankingDecision(rankingDecision);
    }

    @Override
    public RankTitle insert(String rankTitleName, Long rankingDecisionId) {
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        if (rankingDecision == null) {
            throw new RuntimeException("Ranking Decision doesn't exist!");
        }

        if (rankTitleName.isBlank()) {
            throw new RuntimeException("Rank Title name must not be blank!");
        }

        rankTitleName = StringUtil.removeExtraSpaces(rankTitleName);

        if (rankTitleRepository.existsByNameAndRankingDecision(rankTitleName, rankingDecision)) {
            throw new RuntimeException("Rank Title name existed!");
        }

        RankTitle rankTitle = new RankTitle();
        rankTitle.setName(rankTitleName);
        rankTitle.setRankingDecision(rankingDecision);
        return rankTitleRepository.save(rankTitle);
    }


    @Override
    public boolean isExistedByNameAndRankingDecision(String name, RankingDecision rankingDecision) {
        return rankTitleRepository.existsByNameAndRankingDecision(name, rankingDecision);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, isolation = Isolation.SERIALIZABLE)
    public void modifyRankTitleConfig(Long rankingDecisionId, List<String> rankTitlesToRemove,
                                      Map<String, List<String>> rankTitlesToUpdate) {
        RankingDecision rankingDecision = rankingDecisionService.findOne(rankingDecisionId);
        if(rankingDecision == null){
            throw new EntityNotFoundException("Ranking Decision doesn't exist!");
        }else if(rankingDecision.getStatus()){
            throw new FinalizedRankingDecisionException("Cannot edit finalized Ranking Decision.");
        }

        List<String> toRemove = rankTitlesToRemove == null ? new ArrayList<>() : rankTitlesToRemove;
        //remove all repeated element if exist
        toRemove = toRemove.stream().distinct().collect(Collectors.toList());
        //xóa các ranktitle có id trong toRemove
        for (String rankTitleIdStr : toRemove
        ) {
            Long rankTitleId=0L;
            try{
                rankTitleId = Long.parseLong(rankTitleIdStr);
            }catch (NumberFormatException e){
                continue;
            }
            RankTitle rankTitle = rankTitleRepository.findById(rankTitleId)
                    .orElseThrow(() -> new EntityNotFoundException("Rank Title not found!"));

            // xóa các liên kết trong bảng phụ rank_title_option
            rankTitle.setOptions(new LinkedHashSet<>());
            rankTitleRepository.save(rankTitle);

            //sau đó xóa rank title
            rankTitleRepository.delete(rankTitle);
        }

        //update các ranktitle trong toUpdate
        //element format: rankTitleId-optionId[]
        //or rankTitleName-optionId[] if it's a new rankTitle
        for (Map.Entry e : rankTitlesToUpdate.entrySet()
        ) {
            List<String> optionIds = (List<String>) e.getValue();
            Long rankTitleId;
            RankTitle rankTitle;

            //parse key sang id, nếu không parse dc thì là new Rank Title => phải thực hiện thêm mới trước
            try {
                rankTitleId = Long.parseLong((String) e.getKey());
                rankTitle = rankTitleRepository.findById(rankTitleId)
                        .orElseThrow(() -> new EntityNotFoundException("Rank Title not found"));
            } catch (NumberFormatException exception) {
                //kiểm tra xem rankTitleName đã tồn tại trong db chưa
                String rankTitleName = StringUtil.removeExtraSpaces((String) e.getKey());
                if (rankTitleRepository.existsByNameAndRankingDecision(rankTitleName, rankingDecision)) {
                    throw new DuplicatedRankTitleNameException("Rank Title name existed!");
                }

                rankTitle = new RankTitle();
                rankTitle.setName(rankTitleName);
                rankTitle.setRankingDecision(rankingDecision);

                rankTitle = rankTitleRepository.save(rankTitle);

                List<Task> tasks = rankTitleTaskRepository.getTasksByRankingDecision(rankingDecisionId);
                //add các liên kết ranktitle-task cho ranktitle mới
                for (Task task : tasks
                ) {
                    RankTitleTask rankTitleTask = new RankTitleTask(
                            new RankTitleTaskId(rankTitle, task),
                            new BigDecimal(0),
                            new BigDecimal(0)
                    );
                    rankTitleTaskRepository.save(rankTitleTask);
                }
            }

            for (String optionIdStr : optionIds
            ) {
                Long optionId = Long.parseLong(optionIdStr);
                Option option = optionRepository.findById(optionId)
                        .orElseThrow(() -> new EntityNotFoundException("Option not found"));

                //nếu đây là 1 rank title mới, chưa có option nào
                if (rankTitle.getOptions() == null) {
                    Set<Option> optionSet = new LinkedHashSet<>();
                    optionSet.add(option);
                    rankTitle.setOptions(optionSet);
                } else {
                    Long criteriaId = option.getCriteria().getId();
                    List<Option> optionToChange = rankTitle.getOptions()
                            .stream().filter(opt -> opt.getCriteria().getId().equals(criteriaId))
                            .collect(Collectors.toList());

                    if (!optionToChange.isEmpty()) {  //thay đổi option hiện có
                        rankTitle.getOptions().remove(optionToChange.get(0)); //xóa cái cũ

                    }
                    rankTitle.getOptions().add(option);// add cái mới
                }
            }

        }

        //Kiểm tra table rỗng
        List<RankTitle> rankTitles = rankTitleRepository.findByRankingDecision(rankingDecision);
        if (rankTitles == null || rankTitles.isEmpty()) {
            throw new EntityNotFoundException("Empty Rank Title List!");
        }

        //Kiểm tra option rỗng
        int numOfCriteria = rankingDecision.getRankingDecisionCriterias().size();
        for (RankTitle rankTitle : rankTitles
        ) {
            if (rankTitle.getOptions().size() != numOfCriteria) {
                throw new UnacceptableOptionException("Option left as blank");
            }
        }

        //kiểm trả trùng rank score

        List<Double> rankScores = new ArrayList<>();

        for (RankTitle rankTitle : rankTitles
        ) {
            Double rankScore = calculateRankScore(rankingDecision, rankTitle);
            if (rankScores.contains(rankScore)) {
                throw new UnacceptableOptionException("Duplicated Rank Score");
            } else {
                rankScores.add(rankScore);
            }
        }

        List<RankTitle> rankTitleList = findByRankingDecision(rankingDecision);
        if(rankTitleList == null || rankTitleList.isEmpty()){
            throw new EmptyDataTableException("Empty table.");
        }
    }

    @Override
    public double calculateRankScore(RankingDecision rankingDecision, RankTitle rankTitle) {
        double sum = 0;
        for (Option option : rankTitle.getOptions()
        ) {
            Criteria criteria = option.getCriteria();
            int weight = criteria.getRankingDecisionCriterias().stream()
                    .filter(rdc -> rdc.getRankingDecisionCriteriaId().getRankingDecision().equals(rankingDecision))
                    .collect(Collectors.toList()).get(0).getWeight();
            int maxScore = criteria.getOptions().stream().findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Not found any Option in Criteria!")).getScore();
            sum += option.getScore() * ((double) weight / maxScore);
        }
        return sum;
    }

}
