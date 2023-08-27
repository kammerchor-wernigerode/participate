package de.vinado.app.participate.management.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.AuthenticatedPrincipal;

@Getter
public class PrincipalAuthenticatedEvent {

    private final Payload payload;

    public PrincipalAuthenticatedEvent(AuthenticatedPrincipal principal) {
        this.payload = new Payload(principal);
    }


    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class Payload {

        @NonNull
        AuthenticatedPrincipal principal;
    }
}
