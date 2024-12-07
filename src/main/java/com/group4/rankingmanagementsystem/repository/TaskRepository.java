package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
        {
    boolean existsByName(String name);
    List<Task> findTaskByGroupId(Long groupId);
    List<Task> findByIdNotIn(Collection<Long> ids);
}
