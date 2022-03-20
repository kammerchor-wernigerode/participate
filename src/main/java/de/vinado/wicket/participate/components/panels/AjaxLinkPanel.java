package de.vinado.wicket.participate.components.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


/**
 * Link Panel, to provide the functionality to use a link in a {@link org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable}.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AjaxLinkPanel extends Panel {

    private AjaxLink ajaxLink;

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param labelModel Label
     */
    public AjaxLinkPanel(final String id, final IModel<String> labelModel) {
        super(id, labelModel);

        ajaxLink = new AjaxLink<Void>("link") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                AjaxLinkPanel.this.onClick(target);
            }
        };
        add(ajaxLink);

        ajaxLink.add(new Label("label", labelModel.getObject()));
    }

    public AjaxLink getAjaxLink() {
        return ajaxLink;
    }

    /**
     * What happens, when you click the link.
     *
     * @param target {@link AjaxRequestTarget}
     */
    public abstract void onClick(final AjaxRequestTarget target);
}
