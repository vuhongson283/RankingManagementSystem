package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.RankingDecisionCriteria;
import com.group4.rankingmanagementsystem.entity.Task;
import com.group4.rankingmanagementsystem.service.TaskService;
import com.group4.rankingmanagementsystem.repository.TaskRepository;
import com.group4.rankingmanagementsystem.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }



    @Override
    public List<Task> getTaskByGroupId(Long groupId) {
        return taskRepository.findTaskByGroupId(groupId);
    }

    @Override
    public void removeTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void addTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public boolean isTaskNameExist(String taskName) {
        return taskRepository.existsByName(taskName);
    }

    @Override
    public List<Task> findTasksExcept(Collection<Task> tasks) {
        if(tasks.isEmpty()){
            return taskRepository.findAll();
        }

        List<Long> ids = new ArrayList<>();

        for (Task e: tasks
        ) {
            ids.add(e.getId());
        }
        return taskRepository.findByIdNotIn(ids);
    }


}
