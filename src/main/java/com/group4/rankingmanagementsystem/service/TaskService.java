package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Task;

import java.util.Collection;
import java.util.List;

public interface TaskService {
    public List<Task> getTaskByGroupId(Long groupId);

    public void removeTaskById(Long id);
    Task getTaskById(Long id);
    void saveTask(Task task);
    void addTask(Task task);
    boolean isTaskNameExist(String taskName);
    List<Task> findTasksExcept(Collection<Task> tasks);
}
