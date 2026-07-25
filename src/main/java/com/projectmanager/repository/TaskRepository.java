package com.projectmanager.repository;

import com.projectmanager.model.Task;
import com.projectmanager.model.TaskPriority;
import com.projectmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Task> findByAssigneeIdOrderByDeadlineAsc(Long assigneeId);

    List<Task> findByStatus(TaskStatus status);

    long countByStatus(TaskStatus status);

    long countByPriority(TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.deadline < :date AND t.status <> 'DONE' ORDER BY t.deadline ASC")
    List<Task> findOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.deadline IS NOT NULL AND t.status <> 'DONE' ORDER BY t.deadline ASC")
    List<Task> findUpcomingDeadlines();

    @Query("SELECT t FROM Task t WHERE t.deadline = :date AND t.status <> 'DONE' ORDER BY t.priority DESC")
    List<Task> findTasksDueOn(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.status <> 'DONE' ORDER BY t.createdAt DESC")
    List<Task> findRecentOpenTasks();
}
