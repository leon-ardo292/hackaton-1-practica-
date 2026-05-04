package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.SalesAggregates;
import com.oreo.insightfactory.model.ReportRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class WeeklyReportListener {

    private final SalesAggregationService aggregationService;
    private final GitHubModelsClient gitHubModelsClient;
    private final JavaMailSender mailSender;
    private final String fromEmail;

    public WeeklyReportListener(
            SalesAggregationService aggregationService,
            GitHubModelsClient gitHubModelsClient,
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromEmail
    ) {
        this.aggregationService = aggregationService;
        this.gitHubModelsClient = gitHubModelsClient;
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Async
    @EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        SalesAggregates aggregates = aggregationService.calculateAggregates(event.from(), event.to(), event.branch());
        String summary = gitHubModelsClient.summarize(buildPrompt(aggregates));
        if (summary == null || summary.isBlank()) {
            summary = fallbackSummary(aggregates);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.emailTo());
        message.setSubject("Reporte Semanal Oreo - " + toDate(event.from()) + " a " + toDate(event.to()));
        message.setText(summary + "\n\n" + bodyAggregates(aggregates));
        mailSender.send(message);
    }

    private String buildPrompt(SalesAggregates aggregates) {
        return "Con estos datos: totalUnits=%d, totalRevenue=%s, topSku=%s, topBranch=%s. Devuelve un resumen <=120 palabras para enviar por email."
                .formatted(aggregates.totalUnits(), aggregates.totalRevenue(), aggregates.topSku(), aggregates.topBranch());
    }

    private String fallbackSummary(SalesAggregates aggregates) {
        if (aggregates.totalUnits() == 0) {
            return "No se registraron ventas en el rango solicitado. Recomendacion: validar la carga de ventas o ampliar el periodo de analisis.";
        }
        return "Se vendieron %d unidades y se recaudo %s. El SKU top fue %s y la sucursal top fue %s. Recomendacion: reforzar stock del producto lider y revisar promociones por sucursal."
                .formatted(aggregates.totalUnits(), aggregates.totalRevenue(), aggregates.topSku(), aggregates.topBranch());
    }

    private String bodyAggregates(SalesAggregates aggregates) {
        return """
                Agregados:
                totalUnits=%d
                totalRevenue=%s
                topSku=%s
                topBranch=%s
                """.formatted(aggregates.totalUnits(), aggregates.totalRevenue(), aggregates.topSku(), aggregates.topBranch());
    }

    private LocalDate toDate(java.time.Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
