package de.vinado.wicket.participate.ui.form;

import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.behavoir.AutosizeBehavior;
import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.Attribute;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PersonInformationPanel extends BootstrapModalPanel<Attribute> {

    // TODO: Uebersetzung aktualisieren (HTML)

    public PersonInformationPanel(final BootstrapModal modal, final IModel<Attribute> model) {
        super(modal, new ResourceModel("account.personal-details", "Personal Details"), model);

        final CheckBox isVegetarianCb = new CheckBox("attr.vegetarian");
        isVegetarianCb.setLabel(new ResourceModel("lookAtMeImSpecial", "I am vegetarian/vegan"));
        isVegetarianCb.add(BootstrapHorizontalFormDecorator.decorate());
        inner.add(isVegetarianCb);

        final TextArea intelorancesTa = new TextArea("attr.intolerances");
        intelorancesTa.setLabel(new ResourceModel("intolerances", "Intolerances"));
        intelorancesTa.add(BootstrapHorizontalFormDecorator.decorate());
        intelorancesTa.add(new AutosizeBehavior());
        inner.add(intelorancesTa);

        final CheckBox hasCarCb = new CheckBox("attr.car");
        hasCarCb.setLabel(new ResourceModel("iOwnACar", "I own a car"));
        hasCarCb.add(BootstrapHorizontalFormDecorator.decorate());
        inner.add(hasCarCb);

        final TextField streetTf = new TextField("address.streetAddress");
        streetTf.setLabel(new ResourceModel("street", "Street"));
        inner.add(streetTf);

        final TextField zipCodeTf = new TextField("address.postalCode");
        zipCodeTf.setLabel(new ResourceModel("zipCode", "ZIP Code"));
        inner.add(zipCodeTf);

        final TextField cityTf = new TextField("address.locality");
        cityTf.setLabel(new ResourceModel("city", "City"));
        inner.add(cityTf);
    }

    @Override
    protected void onSaveSubmit(final IModel<Attribute> model, final AjaxRequestTarget target) {
        Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
    }
}
