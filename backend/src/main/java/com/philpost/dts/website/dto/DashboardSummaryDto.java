package com.philpost.dts.website.dto;

public record DashboardSummaryDto(int awaitingAction, int overdue, int drafts, int inRouting, int forRevision) {}