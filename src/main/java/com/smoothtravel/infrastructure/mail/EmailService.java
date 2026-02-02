package com.smoothtravel.infrastructure.mail;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class);

    @Inject
    ReactiveMailer mailer;

    public void sendHtml(String to, String subject, String htmlBody) {
        mailer.send(Mail.withHtml(to, subject, htmlBody))
                .subscribe().with(
                        success -> LOG.debugf("Email sent to %s", to),
                        failure -> LOG.errorf(failure, "Failed to send email to %s", to)
                );
    }

    public void sendText(String to, String subject, String textBody) {
        mailer.send(Mail.withText(to, subject, textBody))
                .subscribe().with(
                        success -> LOG.debugf("Email sent to %s", to),
                        failure -> LOG.errorf(failure, "Failed to send email to %s", to)
                );
    }
}
