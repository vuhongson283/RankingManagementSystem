package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.exception.EmptyDataTableException;
import com.group4.rankingmanagementsystem.repository.RankTitleRepository;
import com.group4.rankingmanagementsystem.repository.RankTitleTaskRepository;
import com.group4.rankingmanagementsystem.repository.RankingDecisionRepository;
import com.group4.rankingmanagementsystem.repository.TaskRepository;
import com.group4.rankingmanagementsystem.service.RankTitleTaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankTitleTaskServiceImpl implements RankTitleTaskService {

    private final RankTitleTaskRepository rankTitleTaskRepository;
    private final RankingDecisionRepository rankingDecisionRepository;
    private final TaskRepository taskRepository;
    private final RankTitleRepository rankTitleRepository;

    @Autowired
    public RankTitleTaskServiceImpl(RankTitleTaskRepository rankTitleTaskRepository,
                                    RankingDecisionRepository rankingDecisionRepository,
                                    TaskRepository taskRepository,
                                    RankTitleRepository rankTitleRepository) {
        this.rankTitleTaskRepository = rankTitleTaskRepository;
        this.rankingDecisionRepository = rankingDecisionRepository;
        this.taskRepository = taskRepository;
        this.rankTitleRepository = rankTitleRepository;
    }

    @Override
    public List<RankTitleTask> findByRankingDecision(Long rankingDecisionId) {
        return rankTitleTaskRepository.findByRankingDecision(rankingDecisionId);
    }

    @Override
    public List<Task> getTasksByRankingDecision(Long rankingDecisionId) {
        return rankTitleTaskRepository.getTasksByRankingDecision(rankingDecisionId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, isolation = Isolation.SERIALIZABLE)
    public void modifyTaskAndPriceConfig(Long rankingDecisionId,
                                         List<String> tasksToRemove,
                                         List<String> tasksToAdd,
                                         Map<String, String> inWorkingHourToUpdate,
                                         Map<String, String> overtimeToUpdate) {
        RankingDecision rankingDecision = rankingDecisionRepository.findById(rankingDecisionId)
                .orElseThrow(() -> new EntityNotFoundException("Ranking Decision not found."));

        List<String> toRemove = tasksToRemove == null ? new ArrayList<>() : tasksToRemove;
        List<String> toAdd = tasksToAdd == null ? new ArrayList<>() : tasksToAdd;
        toRemove = toRemove.stream().distinct().collect(Collectors.toList());
        toAdd = toAdd.stream().distinct().collect(Collectors.toList());

        //xóa các task đc đánh dấu trong tasksToRemove
        if(!rankingDecision.getStatus()) {
            for (String taskIdStr : toRemove
            ) {
                Long taskId = Long.parseLong(taskIdStr);
                Task task = taskRepository.findById(taskId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found!"));

                //xóa các liên kết rank title task
                for (RankTitle rankTitle : rankingDecision.getRankTitles()
                ) {
                    rankTitleTaskRepository.deleteById(new RankTitleTaskId(rankTitle, task));
                }
            }


            //add các task đc đánh dấu trong tasksToAdd
            for (String taskIdStr : toAdd
            ) {
                if (!tasksToRemove.contains(taskIdStr)) {
                    Long taskId = Long.parseLong(taskIdStr);
                    Task task = taskRepository.findById(taskId)
                            .orElseThrow(() -> new EntityNotFoundException("Task not found!"));

                    //thêm các liên kết rank title task
                    for (RankTitle rankTitle : rankingDecision.getRankTitles()
                    ) {
                        RankTitleTask rankTitleTask = new RankTitleTask(
                                new RankTitleTaskId(rankTitle, task),
                                new BigDecimal(0),
                                new BigDecimal(0)
                        );
                        rankTitleTaskRepository.save(rankTitleTask);
                    }
                }
            }
        }

        //update inWorkingHour
        for (Map.Entry e : inWorkingHourToUpdate.entrySet() //key: rankTitleId-taskId          value: money
        ) {
            String[] rankTitleTaskStr = ((String) e.getKey()).split("-");
            if (!toRemove.contains(rankTitleTaskStr[1])) {
                String inWorkingHourStr = (String) e.getValue();

                Long rankTitleId = Long.parseLong(rankTitleTaskStr[0]);
                Long taskId = Long.parseLong(rankTitleTaskStr[1]);
                BigDecimal inWorkingHour = new BigDecimal(inWorkingHourStr);

                RankTitle rankTitle = rankTitleRepository
                        .findById(rankTitleId).orElseThrow(() -> new EntityNotFoundException("Rank Title not found."));

                Task task = taskRepository
                        .findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found."));

                RankTitleTask rankTitleTask = rankTitleTaskRepository
                        .findById(new RankTitleTaskId(rankTitle, task))
                        .orElseThrow(() -> new EntityNotFoundException("RankTitleTask not found."));
                rankTitleTask.setInWorkingHour(inWorkingHour);
                rankTitleTaskRepository.save(rankTitleTask);
            }
        }

        //update overtime
        for (Map.Entry e : overtimeToUpdate.entrySet() //key: rankTitleId-taskId          value: money
        ) {
            String[] rankTitleTaskStr = ((String) e.getKey()).split("-");
            if (!toRemove.contains(rankTitleTaskStr[1])) {
                String overtimeStr = (String) e.getValue();

                Long rankTitleId = Long.parseLong(rankTitleTaskStr[0]);
                Long taskId = Long.parseLong(rankTitleTaskStr[1]);
                BigDecimal overtime = new BigDecimal(overtimeStr);

                RankTitle rankTitle = rankTitleRepository
                        .findById(rankTitleId).orElseThrow(() -> new EntityNotFoundException("Rank Title not found."));

                Task task = taskRepository
                        .findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found."));

                RankTitleTask rankTitleTask = rankTitleTaskRepository
                        .findById(new RankTitleTaskId(rankTitle, task))
                        .orElseThrow(() -> new EntityNotFoundException("RankTitleTask not found."));
                rankTitleTask.setOvertime(overtime);
                rankTitleTaskRepository.save(rankTitleTask);
            }
        }

        //kiểm tra table rỗng
        List<Task> rankTitleTasks = rankTitleTaskRepository.getTasksByRankingDecision(rankingDecisionId);
        if(rankTitleTasks == null || rankTitleTasks.isEmpty()){
            throw new EmptyDataTableException("Empty table.");
        }
    }
}
