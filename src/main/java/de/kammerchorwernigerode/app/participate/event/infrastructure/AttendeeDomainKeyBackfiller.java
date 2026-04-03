package de.kammerchorwernigerode.app.participate.event.infrastructure;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AttendeeDomainKeyBackfiller {

    private final AttendeeRecordRepository attendeeRecordRepository;
    private final AttendeeDomainKeyFactory attendeeDomainKeyFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SpringLiquibase springLiquibase;

    public AttendeeDomainKeyBackfiller(AttendeeRecordRepository attendeeRecordRepository,
                                       AttendeeDomainKeyFactory attendeeDomainKeyFactory,
                                       ApplicationEventPublisher applicationEventPublisher,
                                       @Autowired(required = false) SpringLiquibase springLiquibase) {
        this.attendeeRecordRepository = attendeeRecordRepository;
        this.attendeeDomainKeyFactory = attendeeDomainKeyFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.springLiquibase = springLiquibase;
    }

    @Transactional
    public void execute() {
        log.debug("Executing attendee domain key backfiller");

        StopWatch stopWatch = new StopWatch(AttendeeDomainKeyBackfiller.class.getSimpleName());
        List<AttendeeRecord> attendees = attendeeRecordRepository.findAllByDomainKeyIsNull();
        if (attendees.isEmpty()) {
            return;
        }

        backfill(attendees, stopWatch);
        applicationEventPublisher.publishEvent(new Migrated(stopWatch, this));
    }

    private void backfill(Collection<AttendeeRecord> attendees, StopWatch stopWatch) {
        log.debug("Backfilling domain keys for {} attendees", attendees.size());

        stopWatch.start("backfill (migrate)");
        for (AttendeeRecord attendee : attendees) {
            backfill(attendee);
        }
        stopWatch.stop();
    }

    private void backfill(AttendeeRecord attendee) {
        try {
            log.trace("Backfill domain key for {}", attendee);

            AttendeeRecord.Id id = attendee.getId();
            UUID domainKey = attendeeDomainKeyFactory.create(id);
            attendee.setDomainKey(domainKey);

            log.trace("Set domainKey={} for {}", domainKey, attendee);
        } catch (Exception e) {
            log.warn("Failed to backfill domain key for {}", attendee, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void onMigrationSucceeded(Migrated event) {
        StopWatch stopWatch = event.getStopWatch();

        migrateDdl(stopWatch);

        if (log.isDebugEnabled()) {
            System.out.print(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        }
        log.info("Backfill completed in {} ms", stopWatch.getTotalTimeMillis());
    }

    private void migrateDdl(StopWatch sw) {
        try {
            sw.start("liquibase (contract)");
            springLiquibase.afterPropertiesSet();
        } catch (LiquibaseException | NullPointerException e) {
            log.warn("Cannot run Liquibase migration. Restart the application ASAP.", e);
        } finally {
            sw.stop();
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    protected void onMigrationFailed(Migrated event) {
        StopWatch stopWatch = event.getStopWatch();

        if (log.isDebugEnabled()) {
            System.out.print(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        }
        log.warn("Backfill failed after {} ms", stopWatch.getTotalTimeMillis());
    }


    @Getter
    protected static class Migrated extends ApplicationEvent {

        private final StopWatch stopWatch;

        public Migrated(StopWatch stopWatch, Object source) {
            super(source);
            this.stopWatch = stopWatch;
        }
    }
}
