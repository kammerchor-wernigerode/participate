package de.vinado.app.participate.notification.email.app;

import de.vinado.app.participate.notification.email.model.Email;
import de.vinado.app.participate.notification.email.model.Recipient;
import de.vinado.app.participate.notification.email.model.Sender;
import de.vinado.app.participate.notification.email.model.Transmission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface SendEmail {

    Email email();

    Transmission[] transmissions(Sender sender);

    static SendEmail.Builder send(Email email) {
        return new SendEmail.Builder(email);
    }


    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    class Builder {

        private final Email email;

        private final List<RecipientList> recipientLists = new ArrayList<>();

        public Builder atOnce(Recipient... recipients) {
            RecipientList recipientList = new RecipientList(recipients);
            recipientLists.add(recipientList);
            return this;
        }

        public Builder individually(Recipient... recipients) {
            Arrays.stream(recipients)
                .map(RecipientList::new)
                .forEach(recipientLists::add);
            return this;
        }

        SendEmail build() {
            return new SendEmail() {

                @Override
                public Email email() {
                    return email;
                }

                @Override
                public Transmission[] transmissions(Sender sender) {
                    return recipientLists.stream()
                        .map(transmit(sender))
                        .toArray(Transmission[]::new);
                }
            };
        }

        private static Function<RecipientList, Transmission> transmit(Sender sender) {
            return list -> new Transmission(sender, list.recipients());
        }


        private record RecipientList(Recipient... recipients) {
        }
    }
}
