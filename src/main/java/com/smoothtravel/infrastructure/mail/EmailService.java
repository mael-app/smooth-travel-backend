package com.smoothtravel.infrastructure.mail;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendHtml(String to, String subject, String htmlBody) {
        mailer.send(Mail.withHtml(to, subject, htmlBody));
    }

    public void sendText(String to, String subject, String textBody) {
        mailer.send(Mail.withText(to, subject, textBody));
    }
}
