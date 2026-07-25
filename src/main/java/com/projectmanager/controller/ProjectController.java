package com.projectmanager.controller;

import com.projectmanager.model.Project;
import com.projectmanager.model.ProjectStatus;
import com.projectmanager.service.ProjectService;
import com.projectmanager.service.TaskService;
import com.projectmanager.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TeamMemberService teamMemberService, TaskService taskService) {
        this.projectService = projectService;
        this.teamMemberService = teamMemberService;
        this.taskService = taskService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("statuses", ProjectStatus.values());
        return "projects/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("members", teamMemberService.findAll());
        model.addAttribute("isNew", true);
        return "projects/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("project") Project project,
                       BindingResult result,
                       @RequestParam(value = "managerId", required = false) Long managerId,
                       @RequestParam(value = "memberIds", required = false) List<Long> memberIds,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("members", teamMemberService.findAll());
            model.addAttribute("isNew", project.getId() == null);
            return "projects/form";
        }

        // Set manager
        if (managerId != null) {
            teamMemberService.findById(managerId).ifPresent(project::setManager);
        } else {
            project.setManager(null);
        }

        // Set members
        if (memberIds != null && !memberIds.isEmpty()) {
            project.getMembers().clear();
            memberIds.forEach(id -> teamMemberService.findById(id).ifPresent(m -> project.getMembers().add(m)));
        } else {
            project.getMembers().clear();
        }

        projectService.save(project);
        redirectAttributes.addFlashAttribute("successMessage", "Project saved successfully!");
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return projectService.findById(id).map(project -> {
            model.addAttribute("project", project);
            model.addAttribute("tasks", taskService.findByProjectId(id));
            return "projects/detail";
        }).orElse("redirect:/projects");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return projectService.findById(id).map(project -> {
            model.addAttribute("project", project);
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("members", teamMemberService.findAll());
            model.addAttribute("isNew", false);
            return "projects/form";
        }).orElse("redirect:/projects");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully!");
        return "redirect:/projects";
    }
}
