package de.vinado.wicket.participate.tasks;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

@Slf4j
class SchedulerUnitTest {

    @Test
    void testScheduler() {
        val trigger = new CronTrigger("0 0 9 ? * SUN");
        val today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        val df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE");
        val yesterday = today.getTime();
        log.info("Yesterday was : " + df.format(yesterday));

        val nextExecutionTime = trigger.nextExecutionTime(
            new TriggerContext() {

                @Override
                public Instant lastScheduledExecution() {
                    return yesterday.toInstant();
                }

                @Override
                public Instant lastActualExecution() {
                    return yesterday.toInstant();
                }

                @Override
                public Instant lastCompletion() {
                    return yesterday.toInstant();
                }
            });

        String message = "Next Execution date: " + df.format(nextExecutionTime);
        log.info(message);
    }
}
