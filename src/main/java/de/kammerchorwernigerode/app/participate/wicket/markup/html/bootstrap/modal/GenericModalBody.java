package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.IModel;

public class GenericModalBody<T> extends ModalBody implements IGenericComponent<T, GenericModalBody<T>> {

    public GenericModalBody(String id) {
        this(id, null);
    }

    public GenericModalBody(String id, IModel<T> model) {
        super(id, model);
    }
}
