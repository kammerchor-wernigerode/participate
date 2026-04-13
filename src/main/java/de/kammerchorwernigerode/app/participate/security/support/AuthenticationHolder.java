package de.kammerchorwernigerode.app.participate.security.support;

import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

public interface AuthenticationHolder extends Supplier<Authentication> {
}
