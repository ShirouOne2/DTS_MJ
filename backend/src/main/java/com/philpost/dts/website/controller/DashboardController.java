package com.philpost.dts.website.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.DashboardSummaryDto;
import com.philpost.dts.website.dto.InboxItemDto;
import com.philpost.dts.website.service.DashboardService;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/inbox")
    public List<InboxItemDto> inbox() {
        return dashboardService.inbox();
    }

    @GetMapping("/dashboard/summary")
    public DashboardSummaryDto summary() {
        return dashboardService.summary();
    }
}