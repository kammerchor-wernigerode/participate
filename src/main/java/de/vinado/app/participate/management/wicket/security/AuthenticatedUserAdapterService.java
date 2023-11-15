package de.vinado.app.participate.management.wicket.security;

import de.vinado.app.participate.management.security.PrincipalAuthenticatedEvent;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
class AuthenticatedUserAdapterService {

    @NonNull
    private final UserRepository userRepository;

    @EventListener
    public void handle(PrincipalAuthenticatedEvent event) {
        PrincipalAuthenticatedEvent.Payload payload = event.getPayload();
        AuthenticatedPrincipal principal = payload.getPrincipal();

        if (userRepository.existsByUsername(principal.getName())) {
            update(principal);
        } else {
            create(principal);
        }
    }

    private void update(AuthenticatedPrincipal principal) {
        userRepository.findByUsername(principal.getName())
            .map(performing(this::patch))
            .ifPresent(userRepository::save);
    }

    private static <T> UnaryOperator<T> performing(Consumer<T> action) {
        return self -> {
            action.accept(self);
            return self;
        };
    }

    private void patch(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!user.getName().equals(authentication.getName())) {
            throw new IllegalStateException();
        }

        user.setAdmin(isAdmin(authentication));
        user.setEnabled(true);
    }

    private void create(AuthenticatedPrincipal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!principal.getName().equals(authentication.getName())) {
            throw new IllegalStateException();
        }

        User user = new User(authentication.getName(), null, isAdmin(authentication), true);
        userRepository.save(user);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(byPrefix("ROLE_"))
            .map(AuthenticatedUserAdapterService::leadingRoleAbsent)
            .anyMatch(equalsIgnoreCase("admin"));
    }

    private Predicate<String> byPrefix(String prefix) {
        return self -> self.startsWith(prefix);
    }

    private static String leadingRoleAbsent(String authority) {
        return authority.replaceFirst("^ROLE_", "");
    }

    private Predicate<String> equalsIgnoreCase(String role) {
        return self -> self.equalsIgnoreCase(role);
    }
}
