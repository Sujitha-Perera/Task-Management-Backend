package com.sujitha.taskmanagement.repository;

import com.sujitha.taskmanagement.entity.Task;
import com.sujitha.taskmanagement.entity.User;
import com.sujitha.taskmanagement.enums.TaskPriority;
import com.sujitha.taskmanagement.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable pageable);

    Page<Task> findByUserAndPriority(User user, TaskPriority priority, Pageable pageable);

    Page<Task> findByUserAndStatusAndPriority(User user, TaskStatus status, TaskPriority priority, Pageable pageable);
}