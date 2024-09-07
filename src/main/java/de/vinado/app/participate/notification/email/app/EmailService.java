package de.vinado.app.participate.notification.email.app;

import de.vinado.app.participate.notification.email.model.EmailDispatcher;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.notification.email.model.Sender;
import jakarta.mail.internet.InternetAddress;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailDispatcher dispatcher;
    private final Sender sender;

    public EmailService(EmailDispatcher dispatcher, SenderProperties properties) {
        this.dispatcher = dispatcher;
        this.sender = sender(properties);
    }

    private static Sender sender(SenderProperties properties) {
        InternetAddress from = properties.getFrom();
        InternetAddress replyTo = properties.getReplyTo();
        return new Sender(from, replyTo);
    }

    public void execute(SendEmail.Builder command) throws EmailException {
        execute(command.build());
    }

    public void execute(SendEmail command) throws EmailException {
        dispatcher.dispatch(command.email(), command.transmissions(sender));
    }
}
