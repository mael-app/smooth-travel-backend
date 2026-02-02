package com.smoothtravel.infrastructure.mail;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    ReactiveMailer mailer;

    @InjectMocks
    EmailService emailService;

    @Test
    void shouldSendHtmlEmail() {
        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        emailService.sendHtml("user@example.com", "Welcome", "<h1>Hello</h1>");

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(mailer).send(captor.capture());

        Mail sent = captor.getValue();
        assertEquals("user@example.com", sent.getTo().get(0));
        assertEquals("Welcome", sent.getSubject());
        assertNotNull(sent.getHtml());
    }

    @Test
    void shouldSendTextEmail() {
        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        emailService.sendText("user@example.com", "Welcome", "Hello");

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(mailer).send(captor.capture());

        Mail sent = captor.getValue();
        assertEquals("user@example.com", sent.getTo().get(0));
        assertEquals("Welcome", sent.getSubject());
        assertNotNull(sent.getText());
    }
}
