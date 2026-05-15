package de.kammerchorwernigerode.app.participate.wicket;

import org.springframework.boot.web.error.ErrorPage;
import org.springframework.boot.web.error.ErrorPageRegistrar;
import org.springframework.boot.web.error.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
class WicketErrorPageRegistrar implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(
            new ErrorPage(HttpStatus.FORBIDDEN, "/forbidden"),
            new ErrorPage(HttpStatus.NOT_FOUND, "/not-found"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"),
            new ErrorPage(Throwable.class, "/error")
        );
    }
}
