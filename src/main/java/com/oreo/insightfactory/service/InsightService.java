package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.GroupSnapshot;
import com.oreo.insightfactory.dto.SalesSnapshot;
import com.oreo.insightfactory.dto.SummaryRequest;
import com.oreo.insightfactory.dto.SummaryResponse;
import com.oreo.insightfactory.model.InsightEmailEvent;
import com.oreo.insightfactory.repository.SalesSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private final SalesSnapshotRepository snapshotRepository;
    private final GitHubModelsClient gitHubModelsClient;
    private final ApplicationEventPublisher eventPublisher;
    private final String defaultRecipient;

    public InsightService(
            SalesSnapshotRepository snapshotRepository,
            GitHubModelsClient gitHubModelsClient,
            ApplicationEventPublisher eventPublisher,
            @Value("${app.mail.to}") String defaultRecipient
    ) {
        this.snapshotRepository = snapshotRepository;
        this.gitHubModelsClient = gitHubModelsClient;
        this.eventPublisher = eventPublisher;
        this.defaultRecipient = defaultRecipient;
    }

    public SummaryResponse generateSummary(SummaryRequest request) {
        SummaryRequest safeRequest = request == null ? new SummaryRequest(false, null) : request;
        SalesSnapshot snapshot = snapshotRepository.loadSnapshot();
        String prompt = buildPrompt(snapshot);
        String summary = gitHubModelsClient.summarize(prompt);
        if (summary == null || summary.isBlank()) {
            summary = fallbackSummary(snapshot);
        }

        boolean emailQueued = false;
        if (safeRequest.shouldSendEmail()) {
            String recipient = safeRequest.recipient() == null || safeRequest.recipient().isBlank()
                    ? defaultRecipient
                    : safeRequest.recipient();
            eventPublisher.publishEvent(new InsightEmailEvent(recipient, summary));
            emailQueued = true;
        }

        return new SummaryResponse(summary, emailQueued, Instant.now(), snapshot);
    }

    private String buildPrompt(SalesSnapshot snapshot) {
        return """
                Genera un resumen ejecutivo de maximo 4 frases con:
                - SKU lider
                - sucursal lider
                - ticket o ingreso total
                - recomendacion accionable

                Datos:
                totalSales=%d
                totalUnits=%d
                totalRevenue=%s
                byBranch=%s
                bySku=%s
                """.formatted(
                snapshot.totalSales(),
                snapshot.totalUnits(),
                snapshot.totalRevenue(),
                describe(snapshot.byBranch()),
                describe(snapshot.bySku())
        );
    }

    private String fallbackSummary(SalesSnapshot snapshot) {
        if (snapshot.totalSales() == 0) {
            return "Todavia no hay ventas registradas. Registra ventas por sucursal y SKU para generar insights accionables.";
        }

        String topBranch = snapshot.byBranch().isEmpty() ? "sin sucursal" : snapshot.byBranch().getFirst().name();
        String topSku = snapshot.bySku().isEmpty() ? "sin SKU" : snapshot.bySku().getFirst().name();
        return "Se registraron %d ventas y %d unidades, con ingresos totales de %s. La sucursal lider es %s y el SKU lider es %s. Recomendacion: reforzar stock del SKU lider y revisar si la sucursal con menor venta necesita promocion."
                .formatted(snapshot.totalSales(), snapshot.totalUnits(), snapshot.totalRevenue(), topBranch, topSku);
    }

    private String describe(Iterable<GroupSnapshot> groups) {
        return java.util.stream.StreamSupport.stream(groups.spliterator(), false)
                .map(group -> "%s: units=%d, revenue=%s".formatted(group.name(), group.units(), group.revenue()))
                .collect(Collectors.joining(" | "));
    }
}
