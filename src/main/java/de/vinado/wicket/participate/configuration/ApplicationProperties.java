package de.vinado.wicket.participate.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Reads the properties from "classpath:/application.properties"
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Validated
@Configuration
@ConfigurationProperties("app")
@EnableConfigurationProperties
public class ApplicationProperties {

    private boolean developmentMode = false;

    private String customer = "";

    @NotNull
    private String participatePassword;

    private String version;

    private Mail mail;

    private Database database;

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(final boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(final String customer) {
        this.customer = customer;
    }

    public String getParticipatePassword() {
        return participatePassword;
    }

    public void setParticipatePassword(final String participatePassword) {
        this.participatePassword = participatePassword;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(final Mail mail) {
        this.mail = mail;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(final Database database) {
        this.database = database;
    }

    public static class Mail {

        @NotNull
        private String sender;

        private String footer = "Participate";

        public String getSender() {
            return sender;
        }

        public void setSender(final String sender) {
            this.sender = sender;
        }

        public String getFooter() {
            return footer;
        }

        public void setFooter(final String footer) {
            this.footer = footer;
        }
    }

    public static class Database {

        private boolean mirrorRemoteDatabase = false;

        private String remoteUrl;

        private String remoteUsername;

        private String remotePassword;

        public boolean isMirrorRemoteDatabase() {
            return mirrorRemoteDatabase;
        }

        public void setMirrorRemoteDatabase(final boolean mirrorRemoteDatabase) {
            this.mirrorRemoteDatabase = mirrorRemoteDatabase;
        }

        public String getRemoteUrl() {
            return remoteUrl;
        }

        public void setRemoteUrl(final String remoteUrl) {
            this.remoteUrl = remoteUrl;
        }

        public String getRemoteUsername() {
            return remoteUsername;
        }

        public void setRemoteUsername(final String remoteUsername) {
            this.remoteUsername = remoteUsername;
        }

        public String getRemotePassword() {
            return remotePassword;
        }

        public void setRemotePassword(final String remotePassword) {
            this.remotePassword = remotePassword;
        }
    }
}
