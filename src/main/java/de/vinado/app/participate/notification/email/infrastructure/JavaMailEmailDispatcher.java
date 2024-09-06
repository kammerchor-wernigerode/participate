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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Slf4j
@Scope(SCOPE_SINGLETON)
@Component
@EnableConfigurationProperties(JavaMailDispatcherProperties.class)
public class JavaMailEmailDispatcher implements EmailDispatcher, InitializingBean, DisposableBean {

    private final JavaMailSender sender;
    private final ExecutorService executor;
    private final JavaMailDispatcherProperties properties;

    private final BlockingQueue<DispatchTask> queue = new LinkedBlockingDeque<>();

    public JavaMailEmailDispatcher(JavaMailSender sender, JavaMailDispatcherProperties properties) {
        this.sender = sender;
        this.executor = createExecutorService(properties);
        this.properties = properties;
    }

    private static ExecutorService createExecutorService(JavaMailDispatcherProperties properties) {
        try {
            int concurrentSessions = properties.getConcurrentSessions();
            return Executors.newFixedThreadPool(concurrentSessions);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Number of concurrent sessions must be greater than zero");
        }
    }

    @Override
    public void afterPropertiesSet() {
        for (int i = 0; i < properties.getConcurrentSessions(); i++) {
            Worker worker = new Worker();
            executor.execute(worker);
        }
    }

    @Override
    public void destroy() {
        try {
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

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DispatchTask task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @RequiredArgsConstructor
    private class DispatchTask implements Runnable {

        private final Email email;
        private final Transmission transmission;

        @Override
        public void run() {
            dispatch(email, transmission);
        }

        @SneakyThrows
        public void dispatch(Email email, Transmission transmission) {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setSubject(email.subject());
            helper.setFrom(transmission.sender().address());

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
            log.debug("Email sent to {}", transmission);
        }
    }
}
