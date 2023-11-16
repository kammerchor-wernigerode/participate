package de.vinado.wicket.participate.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class SingerUpdateEvent implements Serializable {

    private final AjaxRequestTarget target;
}
