package com.projectmanager.controller;

import com.projectmanager.model.Task;
import com.projectmanager.model.TaskPriority;
import com.projectmanager.model.TaskStatus;
import com.projectmanager.service.ProjectService;
import com.projectmanager.service.TaskService;
import com.projectmanager.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final com.projectmanager.repository.TaskCommentRepository commentRepository;

    public TaskController(TaskService taskService, ProjectService projectService, TeamMemberService teamMemberService, com.projectmanager.repository.TaskCommentRepository commentRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.teamMemberService = teamMemberService;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(value = "projectId", required = false) Long projectId, Model model) {
        Task task = new Task();
        if (projectId != null) {
            projectService.findById(projectId).ifPresent(task::setProject);
        }
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("members", teamMemberService.findAll());
        model.addAttribute("isNew", true);
        return "tasks/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("task") Task task,
                       BindingResult result,
                       @RequestParam("projectId") Long projectId,
                       @RequestParam(value = "assigneeId", required = false) Long assigneeId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projects", projectService.findAll());
            model.addAttribute("members", teamMemberService.findAll());
            model.addAttribute("isNew", task.getId() == null);
            return "tasks/form";
        }

        // Set project
        projectService.findById(projectId).ifPresent(task::setProject);

        // Set assignee
        if (assigneeId != null) {
            teamMemberService.findById(assigneeId).ifPresent(task::setAssignee);
        } else {
            task.setAssignee(null);
        }

        taskService.save(task);
        redirectAttributes.addFlashAttribute("successMessage", "Task saved successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return taskService.findById(id).map(task -> {
            model.addAttribute("task", task);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("projects", projectService.findAll());
            model.addAttribute("members", teamMemberService.findAll());
            model.addAttribute("isNew", false);
            return "tasks/form";
        }).orElse("redirect:/tasks");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        taskService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") TaskStatus status,
                               RedirectAttributes redirectAttributes) {
        taskService.findById(id).ifPresent(task -> {
            task.setStatus(status);
            taskService.save(task);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Task status updated!");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        return taskService.findById(id).map(task -> {
            model.addAttribute("task", task);
            model.addAttribute("members", teamMemberService.findAll());
            return "tasks/detail";
        }).orElse("redirect:/tasks");
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam("text") String text, @RequestParam("authorId") Long authorId, RedirectAttributes redirectAttributes) {
        taskService.findById(id).ifPresent(task -> {
            teamMemberService.findById(authorId).ifPresent(author -> {
                com.projectmanager.model.TaskComment comment = new com.projectmanager.model.TaskComment();
                comment.setTask(task);
                comment.setAuthor(author);
                comment.setText(text);
                commentRepository.save(comment);
            });
        });
        redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully!");
        return "redirect:/tasks/" + id;
    }
}
