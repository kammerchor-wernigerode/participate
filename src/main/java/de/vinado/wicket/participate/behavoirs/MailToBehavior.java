package de.vinado.wicket.participate.behavoirs;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MailToBehavior extends Behavior {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailToBehavior.class);

    private String recipientEmail;

    private String recipientName;

    private String subject;

    private boolean valid = false;

    public MailToBehavior(final String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public MailToBehavior(final String recipientEmail, final String recipientName) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
    }

    public MailToBehavior(final String recipientEmail, final String recipientName, final String subject) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
    }

    @Override
    public void beforeRender(final Component component) {
        String mailToString = "";

        if (!Strings.isEmpty(recipientEmail)) {
            mailToString = "mailto:" + recipientEmail;
        }
        if (!Strings.isEmpty(recipientName) && !Strings.isEmpty(recipientEmail)) {
            mailToString = "mailto:" + recipientName + "<" + recipientEmail + ">";
            if (!Strings.isEmpty(subject)) {
                mailToString = mailToString + "?subject=" + subject;
            }
        }

        if (!Strings.isEmpty(mailToString)) {
            component.getResponse().write("<a href=\"" + UriUtils.encodeQuery(mailToString, "UTF-8") + "\" class=\"nobusy\"");
            valid = true;
        }
    }

    @Override
    public void afterRender(final Component component) {
        if (valid)
            component.getResponse().write("</a>");
    }
}
