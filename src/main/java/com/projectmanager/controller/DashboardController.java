package com.projectmanager.controller;

import com.projectmanager.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        model.addAllAttributes(stats);
        return "dashboard";
    }
}
