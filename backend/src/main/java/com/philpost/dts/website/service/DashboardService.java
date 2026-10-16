package com.philpost.dts.website.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.dto.DashboardSummaryDto;
import com.philpost.dts.website.dto.InboxItemDto;
import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.RouteStep;
import com.philpost.dts.website.entity.StepStatus;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.repository.DocumentRepository;
import com.philpost.dts.website.repository.RouteStepRepository;


@Service
public class DashboardService {

    private final RouteStepRepository routeStepRepository;
    private final DocumentRepository documentRepository;
    private final CurrentUserService currentUserService;
    private final DocumentAccessService access;

    public DashboardService(RouteStepRepository routeStepRepository, DocumentRepository documentRepository,
                            CurrentUserService currentUserService, DocumentAccessService access) {
        this.routeStepRepository = routeStepRepository;
        this.documentRepository = documentRepository;
        this.currentUserService = currentUserService;
        this.access = access;
    }

    @Transactional(readOnly = true)
    public List<InboxItemDto> inbox() {
        User user = currentUserService.get();
        LocalDate today = LocalDate.now();
        return routeStepRepository.findInbox(user.getDepartment().getDepartmentId(), StepStatus.ACTIVE)
                .stream().map(s -> toItem(s, user, today)).toList();
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto summary() {
        User user = currentUserService.get();
        Long dept = user.getDepartment().getDepartmentId();
        boolean all = access.seesAll(user);
        return new DashboardSummaryDto(
                (int) routeStepRepository.countForDepartment(dept, StepStatus.ACTIVE),
                (int) routeStepRepository.countOverdue(dept, StepStatus.ACTIVE, LocalDate.now()),
                visibleCount(all, dept, DocumentStatus.DRAFT),
                visibleCount(all, dept, DocumentStatus.IN_ROUTING),
                visibleCount(all, dept, DocumentStatus.FOR_REVISION));
    }

    // Uses the same query as My Docs, so a counter always matches the list it links to.
    private int visibleCount(boolean seesAll, Long departmentId, DocumentStatus status) {
        return (int) documentRepository.search(seesAll, departmentId, status, null, PageRequest.of(0, 1))
                .getTotalElements();
    }

    
    private InboxItemDto toItem(RouteStep s, User user, LocalDate today) {
        Document d = s.getRoute().getDocument();
        return new InboxItemDto(d.getDocumentId(), d.getReferenceNo(), d.getTitle(),
                d.getDocumentType().getName(), d.getOriginDepartment().getName(),
                s.getRoute().getRouteType().name(), s.getRoute().getRoundNo(),
                s.getStepId(), s.getActionCode().getCode(), s.getActionCode().getLabel(),
                s.getActionCode().isRequiresSignature(),
                s.getActionCode().getFileKind() != null
                        ? s.getActionCode().getFileKind().name() : null,
                s.getInstruction(), s.getDueDate(),
                s.getDueDate() != null && s.getDueDate().isBefore(today),
                s.getActivatedAt(), s.getReceivedAt(), access.stepActions(s, user));
    }

}