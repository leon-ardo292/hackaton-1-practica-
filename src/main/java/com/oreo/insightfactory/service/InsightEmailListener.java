package com.oreo.insightfactory.service;

import com.oreo.insightfactory.model.InsightEmailEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class InsightEmailListener {

    private final JavaMailSender mailSender;
    private final String from;

    public InsightEmailListener(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Async
    @EventListener
    public void sendInsightEmail(InsightEmailEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.recipient());
        message.setSubject("Oreo sales insight");
        message.setText(event.summary());
        mailSender.send(message);
    }
}
