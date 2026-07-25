package com.projectmanager.service;

import com.projectmanager.model.*;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final TeamMemberService teamMemberService;

    public DashboardService(ProjectService projectService, TaskService taskService, TeamMemberService teamMemberService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.teamMemberService = teamMemberService;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Counts
        stats.put("totalProjects", projectService.count());
        stats.put("totalTasks", taskService.count());
        stats.put("totalMembers", teamMemberService.count());

        // Task counts by status
        stats.put("todoTasks", taskService.countByStatus(TaskStatus.TODO));
        stats.put("inProgressTasks", taskService.countByStatus(TaskStatus.IN_PROGRESS));
        stats.put("inReviewTasks", taskService.countByStatus(TaskStatus.IN_REVIEW));
        stats.put("doneTasks", taskService.countByStatus(TaskStatus.DONE));

        // Task counts by priority
        stats.put("lowPriorityTasks", taskService.countByPriority(TaskPriority.LOW));
        stats.put("mediumPriorityTasks", taskService.countByPriority(TaskPriority.MEDIUM));
        stats.put("highPriorityTasks", taskService.countByPriority(TaskPriority.HIGH));
        stats.put("criticalPriorityTasks", taskService.countByPriority(TaskPriority.CRITICAL));

        // Overdue
        List<Task> overdueTasks = taskService.findOverdueTasks();
        stats.put("overdueTasks", overdueTasks);
        stats.put("overdueCount", overdueTasks.size());

        // Upcoming deadlines (top 5)
        List<Task> upcoming = taskService.findUpcomingDeadlines();
        stats.put("upcomingDeadlines", upcoming.stream().limit(5).toList());

        // Tasks due today
        List<Task> todayTasks = taskService.findTasksDueToday();
        stats.put("todayTasks", todayTasks);
        stats.put("todayTaskCount", todayTasks.size());

        // Recent open tasks (top 5)
        List<Task> recentOpen = taskService.findRecentOpenTasks();
        stats.put("recentOpenTasks", recentOpen.stream().limit(5).toList());

        // Recent projects (top 5)
        List<Project> recentProjects = projectService.findAll();
        stats.put("recentProjects", recentProjects.stream().limit(5).toList());

        // Project counts by status
        stats.put("planningProjects", projectService.countByStatus(ProjectStatus.PLANNING));
        stats.put("activeProjects", projectService.countByStatus(ProjectStatus.IN_PROGRESS));
        stats.put("completedProjects", projectService.countByStatus(ProjectStatus.COMPLETED));

        // Team members list (for workload widget)
        List<TeamMember> members = teamMemberService.findAll();
        stats.put("teamMembers", members);

        return stats;
    }
}
