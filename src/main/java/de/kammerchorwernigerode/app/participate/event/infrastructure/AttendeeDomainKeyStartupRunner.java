package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AttendeeDomainKeyStartupRunner implements ApplicationRunner {

    private final AttendeeDomainKeyBackfiller attendeeDomainKeyBackfiller;

    @Override
    public void run(ApplicationArguments args) {
        attendeeDomainKeyBackfiller.execute();
    }
}
