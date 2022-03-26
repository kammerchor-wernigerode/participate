package de.vinado.wicket.participate.components.modals;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * A Bootstrap styled modal panel which contains a close button.
 *
 * @author Vincent Nadoll
 */
@Deprecated
public class DismissableBootstrapModalPanel<T> extends GenericPanel<T> {

    protected static final String TITLE_LABEL_ID = "title";
    protected static final String CONTENT_LABEL_ID = "content";
    protected static final String CLOSE_BUTTON_ID = "close";

    private final BootstrapModal modal;

    /**
     * @param modal      the modal in which this panel is embedded
     * @param titleModel the title model
     * @param model      the modal which contains the panel content
     */
    public DismissableBootstrapModalPanel(BootstrapModal modal, IModel<String> titleModel, IModel<T> model) {
        super(modal.getModalId(), model);

        this.modal = modal;

        add(newTitleLabel(TITLE_LABEL_ID, titleModel));
        add(newContentComponent(CONTENT_LABEL_ID, model));
        add(newCloseButton(CLOSE_BUTTON_ID));
    }

    /**
     * @param id    the title label Wicket ID
     * @param model the label model
     * @return new label component
     */
    private Label newTitleLabel(String id, IModel<String> model) {
        return new Label(id, model);
    }

    /**
     * @param id    the content component Wicket ID
     * @param model the content model
     * @return new content component
     */
    private Component newContentComponent(String id, IModel<T> model) {
        return new MultiLineLabel(id, model);
    }

    /**
     * @param id close button Wicket ID
     * @return new close button component
     */
    private AbstractLink newCloseButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.close(target);
                onAfterClose(target);
            }
        };
    }

    protected void onAfterClose(AjaxRequestTarget target) {
        modal.onAfterClose(modal, target);
    }
}
