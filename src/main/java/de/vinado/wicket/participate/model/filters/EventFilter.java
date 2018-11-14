package de.vinado.wicket.participate.model.filters;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class EventFilter implements Serializable {

    private String searchTerm;
    private Date startDate;
    private Date endDate;
    private boolean showAll;
}
