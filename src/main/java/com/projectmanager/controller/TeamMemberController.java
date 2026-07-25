package com.projectmanager.controller;

import com.projectmanager.model.Role;
import com.projectmanager.model.TeamMember;
import com.projectmanager.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/team")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", teamMemberService.findAll());
        model.addAttribute("roles", Role.values());
        return "team/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new TeamMember());
        model.addAttribute("roles", Role.values());
        model.addAttribute("isNew", true);
        return "team/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("member") TeamMember member,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        // Check email uniqueness
        if (teamMemberService.isEmailTaken(member.getEmail(), member.getId())) {
            result.rejectValue("email", "duplicate", "This email is already in use");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("isNew", member.getId() == null);
            return "team/form";
        }

        teamMemberService.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "Team member saved successfully!");
        return "redirect:/team";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return teamMemberService.findById(id).map(member -> {
            model.addAttribute("member", member);
            model.addAttribute("roles", Role.values());
            model.addAttribute("isNew", false);
            return "team/form";
        }).orElse("redirect:/team");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        teamMemberService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Team member removed successfully!");
        return "redirect:/team";
    }
}
