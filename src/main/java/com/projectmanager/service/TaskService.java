package com.projectmanager.service;

import com.projectmanager.model.Task;
import com.projectmanager.model.TaskPriority;
import com.projectmanager.model.TaskStatus;
import com.projectmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public List<Task> findByProjectId(Long projectId) {
        return repository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }

    public long countByStatus(TaskStatus status) {
        return repository.countByStatus(status);
    }

    public long countByPriority(TaskPriority priority) {
        return repository.countByPriority(priority);
    }

    public List<Task> findOverdueTasks() {
        return repository.findOverdueTasks(LocalDate.now());
    }

    public List<Task> findUpcomingDeadlines() {
        return repository.findUpcomingDeadlines();
    }

    public List<Task> findTasksDueToday() {
        return repository.findTasksDueOn(LocalDate.now());
    }

    public List<Task> findRecentOpenTasks() {
        return repository.findRecentOpenTasks();
    }
}

