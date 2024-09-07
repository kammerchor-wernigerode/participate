package de.vinado.app.participate.notification.email.infrastructure;

import de.vinado.app.participate.notification.email.model.Email;
import de.vinado.app.participate.notification.email.model.EmailDispatcher;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.notification.email.model.Recipient;
import de.vinado.app.participate.notification.email.model.Transmission;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Slf4j
@Scope(SCOPE_SINGLETON)
@Component
@EnableConfigurationProperties(JavaMailDispatcherProperties.class)
class JavaMailDispatcher implements EmailDispatcher, InitializingBean, DisposableBean {

    private final JavaMailSender sender;

    private final JavaMailDispatcherProperties properties;

    private final BlockingQueue<DispatchTask> queue = new LinkedBlockingDeque<>();

    private final ExecutorService executor;

    private final ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Set<Worker> activeWorkers = Collections.synchronizedSet(new HashSet<>());

    private ScheduledFuture<?> monitorTask;

    public JavaMailDispatcher(JavaMailSender sender, JavaMailDispatcherProperties properties) {
        this.sender = sender;
        this.properties = properties;
        this.executor = createExecutorService(properties);
    }

    private static ExecutorService createExecutorService(JavaMailDispatcherProperties properties) {
        try {
            int concurrentSessions = properties.getConcurrentTransmissions();
            return Executors.newFixedThreadPool(concurrentSessions);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Number of concurrent sessions must be greater than zero");
        }
    }

    @Override
    public void afterPropertiesSet() {
        for (int i = 0; i < properties.getConcurrentTransmissions(); i++) {
            registerWorker();
        }

        monitorTask = monitorExecutor.scheduleAtFixedRate(this::monitorWorkers, 0, 1, TimeUnit.MINUTES);
    }

    private void monitorWorkers() {
        synchronized (activeWorkers) {
            for (Worker worker : activeWorkers) {
                if (!worker.running) {
                    activeWorkers.remove(worker);
                    registerWorker();
                }
            }
        }
    }

    private void registerWorker() {
        if (executor.isShutdown()) {
            log.warn("Cannot register worker because the executor has been shut down");
            return;
        }

        Worker worker = new Worker();
        activeWorkers.add(worker);
        executor.execute(worker);
    }

    @Override
    public void destroy() {
        try {
            monitorTask.cancel(true);
            monitorExecutor.shutdown();

            List<Runnable> tasks = executor.shutdownNow();
            log.info("Shutting down email dispatcher with {} pending transmissions", tasks.size());
            if (!executor.awaitTermination(properties.getShutdownGracePeriod().toMillis(), TimeUnit.MILLISECONDS)) {
                log.warn("Email dispatcher did not shut down gracefully within the specified grace period of {}", properties.getShutdownGracePeriod());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void dispatch(Email email, Transmission... transmissions) throws EmailException {
        try {
            for (Transmission transmission : transmissions) {
                DispatchTask task = new DispatchTask(email, transmission);
                queue.put(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EmailException(e);
        } catch (Exception e) {
            throw new EmailException(e);
        }
    }


    @RequiredArgsConstructor
    private class Worker implements Runnable {

        private volatile boolean running = false;

        @Override
        public void run() {
            running = true;

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DispatchTask task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            running = false;
        }
    }

    @RequiredArgsConstructor
    private class DispatchTask implements Runnable {

        private final Email email;

        private final Transmission transmission;

        @Override
        public void run() {
            Recipient[] recipients = transmission.recipients();
            int batchSize = properties.getRecipientThreshold();
            int totalRecipients = recipients.length;

            for (int i = 0; i < totalRecipients; i += batchSize) {
                int end = Math.min(totalRecipients, i + batchSize);
                Recipient[] batch = Arrays.copyOfRange(recipients, i, end);
                Transmission transmission = new Transmission(this.transmission.sender(), batch);
                dispatch(email, transmission);
            }
        }

        @SneakyThrows
        public void dispatch(Email email, Transmission transmission) {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setSubject(email.subject());
            helper.setFrom(transmission.sender().from());

            Optional<InternetAddress> replyTo = transmission.sender().replyTo();
            if (replyTo.isPresent()) {
                helper.setReplyTo(replyTo.get());
            }

            for (Recipient recipient : transmission.recipients()) {
                message.addRecipients(recipient.type(), new InternetAddress[]{recipient.address()});
            }

            Optional<String> text = email.textContent();
            if (text.isPresent()) {
                helper.setText(text.get(), false);
            }

            Optional<String> html = email.htmlContent();
            if (html.isPresent()) {
                helper.setText(html.get(), true);
            }

            Iterator<Email.Attachment> attachments = email.attachments().iterator();
            while (attachments.hasNext()) {
                Email.Attachment attachment = attachments.next();
                helper.addAttachment(attachment.name(), attachment, attachment.type().toString());
            }

            sender.send(message);
            log.debug("Email sent to {} from {}", transmission.recipients(), transmission.sender());
        }
    }
}
