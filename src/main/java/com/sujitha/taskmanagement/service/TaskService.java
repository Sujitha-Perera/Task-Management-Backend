package com.sujitha.taskmanagement.service;

import com.sujitha.taskmanagement.dto.TaskCreateRequest;
import com.sujitha.taskmanagement.dto.TaskResponse;
import com.sujitha.taskmanagement.dto.TaskUpdateRequest;
import com.sujitha.taskmanagement.entity.Task;
import com.sujitha.taskmanagement.entity.User;
import com.sujitha.taskmanagement.enums.Role;
import com.sujitha.taskmanagement.enums.TaskPriority;
import com.sujitha.taskmanagement.enums.TaskStatus;
import com.sujitha.taskmanagement.exception.ResourceNotFoundException;
import com.sujitha.taskmanagement.exception.UnauthorizedException;
import com.sujitha.taskmanagement.repository.TaskRepository;
import com.sujitha.taskmanagement.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(TaskCreateRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    public Page<TaskResponse> getTasks(String userEmail,
                                       TaskStatus status,
                                       TaskPriority priority,
                                       int page,
                                       int size,
                                       String sortBy,
                                       String direction) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;

        if (user.getRole() == Role.ADMIN) {
            if (status != null && priority != null) {
                taskPage = taskRepository.findByStatusAndPriority(status, priority, pageable);
            } else if (status != null) {
                taskPage = taskRepository.findByStatus(status, pageable);
            } else if (priority != null) {
                taskPage = taskRepository.findByPriority(priority, pageable);
            } else {
                taskPage = taskRepository.findAll(pageable);
            }
        } else {
            if (status != null && priority != null) {
                taskPage = taskRepository.findByUserAndStatusAndPriority(user, status, priority, pageable);
            } else if (status != null) {
                taskPage = taskRepository.findByUserAndStatus(user, status, pageable);
            } else if (priority != null) {
                taskPage = taskRepository.findByUserAndPriority(user, priority, pageable);
            } else {
                taskPage = taskRepository.findByUser(user, pageable);
            }
        }

        return taskPage.map(this::mapToResponse);
    }

    public TaskResponse getTaskById(Long id, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (user.getRole() != Role.ADMIN && !task.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not allowed to access this task");
        }

        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (user.getRole() != Role.ADMIN && !task.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not allowed to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    public String deleteTask(Long id, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (user.getRole() != Role.ADMIN && !task.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);

        return "Task deleted successfully";
    }

    public TaskResponse markTaskComplete(Long id, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (user.getRole() != Role.ADMIN && !task.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not allowed to update this task");
        }

        task.setStatus(TaskStatus.DONE);

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUser().getEmail()
        );
    }
}