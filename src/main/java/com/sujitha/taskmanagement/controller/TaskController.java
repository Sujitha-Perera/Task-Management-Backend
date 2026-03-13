package com.sujitha.taskmanagement.controller;

import com.sujitha.taskmanagement.dto.TaskCreateRequest;
import com.sujitha.taskmanagement.dto.TaskResponse;
import com.sujitha.taskmanagement.dto.TaskUpdateRequest;
import com.sujitha.taskmanagement.enums.TaskPriority;
import com.sujitha.taskmanagement.enums.TaskStatus;
import com.sujitha.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Principal principal) {
        return ResponseEntity.ok(taskService.createTask(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            Principal principal,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(
                taskService.getTasks(principal.getName(), status, priority, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(taskService.getTaskById(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            Principal principal) {
        return ResponseEntity.ok(taskService.updateTask(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(taskService.deleteTask(id, principal.getName()));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markTaskComplete(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(taskService.markTaskComplete(id, principal.getName()));
    }
}