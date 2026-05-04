package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.WeeklySummaryAcceptedResponse;
import com.oreo.insightfactory.dto.WeeklySummaryRequest;
import com.oreo.insightfactory.handlerexception.ForbiddenOperationException;
import com.oreo.insightfactory.model.AppUser;
import com.oreo.insightfactory.model.ReportRequestedEvent;
import com.oreo.insightfactory.model.UserRole;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class WeeklySummaryService {

    private final CurrentUserService currentUserService;
    private final ApplicationEventPublisher eventPublisher;

    public WeeklySummaryService(CurrentUserService currentUserService, ApplicationEventPublisher eventPublisher) {
        this.currentUserService = currentUserService;
        this.eventPublisher = eventPublisher;
    }

    public WeeklySummaryAcceptedResponse requestSummary(WeeklySummaryRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        String branch = resolveBranch(user, request.branch());
        LocalDate toDate = request.to() == null ? LocalDate.now() : request.to();
        LocalDate fromDate = request.from() == null ? toDate.minusDays(6) : request.from();
        Instant from = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = toDate.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);
        String requestId = "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Instant requestedAt = Instant.now();

        eventPublisher.publishEvent(new ReportRequestedEvent(
                requestId,
                from,
                to,
                branch,
                request.emailTo(),
                user.getUsername()
        ));

        return new WeeklySummaryAcceptedResponse(
                requestId,
                "PROCESSING",
                "Su solicitud de reporte esta siendo procesada. Recibira el resumen en " + request.emailTo() + " en unos momentos.",
                "30-60 segundos",
                requestedAt
        );
    }

    private String resolveBranch(AppUser user, String requestedBranch) {
        if (user.getRole() == UserRole.BRANCH) {
            if (requestedBranch != null && !requestedBranch.isBlank() && !user.getBranch().equalsIgnoreCase(requestedBranch)) {
                throw new ForbiddenOperationException("BRANCH users can only request summaries for their assigned branch");
            }
            return user.getBranch();
        }
        return requestedBranch;
    }
}
