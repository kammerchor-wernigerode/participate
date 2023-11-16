package de.vinado.wicket.participate.components.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class AjaxLinkPanel extends Panel {

    private AjaxLink ajaxLink;

    public AjaxLinkPanel(String id, IModel<String> labelModel) {
        super(id, labelModel);

        ajaxLink = new AjaxLink<Void>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AjaxLinkPanel.this.onClick(target);
            }
        };
        add(ajaxLink);

        ajaxLink.add(new Label("label", labelModel.getObject()));
    }

    public AjaxLink getAjaxLink() {
        return ajaxLink;
    }

    public abstract void onClick(AjaxRequestTarget target);
}
