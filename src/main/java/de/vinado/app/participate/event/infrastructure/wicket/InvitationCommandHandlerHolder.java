package de.vinado.app.participate.event.infrastructure.wicket;

import de.vinado.app.participate.event.app.InvitationCommandHandler;
import de.vinado.app.participate.wicket.spring.Holder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
class InvitationCommandHandlerHolder implements Holder<InvitationCommandHandler> {

    private final InvitationCommandHandler service;
}
