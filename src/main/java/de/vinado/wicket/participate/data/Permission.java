package de.vinado.wicket.participate.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@DiscriminatorValue("permission")
public class Permission extends ListOfValue implements Configurable {

    public final static String CREATE_EVENT = "CREATE_EVENT";
    public final static String EDIT_EVENT = "EDIT_EVENT";
    public final static String REMOVE_EVENT = "REMOVE_EVENT";
    public final static String SHOW_EVENT = "SHOW_EVENT";
    public final static String SHOW_EVENT_DETAILS = "SHOW_EVENT_DETAILS";
    public final static String CREATE_MEMBER = "CREATE_MEMBER";
    public final static String EDIT_MEMBER = "EDIT_MEMBER";
    public final static String REMOVE_MEMBER = "REMOVE_MEMBER";
    public final static String SHOW_MEMBER = "SHOW_MEMBER";

    public Permission() {
    }
}
