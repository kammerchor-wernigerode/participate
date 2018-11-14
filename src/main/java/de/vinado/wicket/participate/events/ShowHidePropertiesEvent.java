package de.vinado.wicket.participate.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@RequiredArgsConstructor
public class ShowHidePropertiesEvent implements Serializable {

    private final AjaxRequestTarget target;
}
