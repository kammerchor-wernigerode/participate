package de.vinado.wicket.participate.configuration;

/**
 * Proprietary but optional application features.
 *
 * @author Vincent Nadoll
 */
public enum Feature {

    /**
     * If enabled, pending invitation will receive a reminder notification.
     *
     * @see de.vinado.wicket.participate.Scheduler
     */
    REMIND_OVERDUE,

    /**
     * If enabled, a list of event attendees will be sent to the club's score's manager.
     */
    NOTIFY_SCORES_MANAGER,
}
