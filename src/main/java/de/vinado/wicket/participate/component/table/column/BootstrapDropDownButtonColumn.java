package de.vinado.wicket.participate.component.table.column;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.AlignmentBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.DropDownButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapDropDownButtonColumn<T, S> extends AbstractColumn<T, S> {

    public BootstrapDropDownButtonColumn() {
        super(Model.of(""));
    }

    @Override
    public void populateItem(final Item<ICellPopulator<T>> cellItem, final String componentId, final IModel<T> rowModel) {
        final DropDownButton dropDownButton = new DropDownButton(componentId, Model.of("")) {
            @Override
            protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
                return BootstrapDropDownButtonColumn.this.newSubMenuButtons(buttonMarkupId, rowModel);
            }
        };
        dropDownButton.setSize(Buttons.Size.Mini);
        dropDownButton.setType(Buttons.Type.Link);
        dropDownButton.setAlignment(AlignmentBehavior.Alignment.RIGHT);
        //dropDownButton.add(new TooltipBehavior(new ResourceModel("more", "More")));
        cellItem.add(dropDownButton);
    }

    @Override
    public String getCssClass() {
        return "width-fix-30";
    }

    public abstract List<AbstractLink> newSubMenuButtons(final String buttonMarkupId, final IModel<T> rowModel);

    protected abstract class DropDownItem extends AjaxLink<T> {

        private IModel<T> rowModel;

        protected DropDownItem(final String id, final IModel<T> rowModel, final IModel<String> labelModel) {
            this(id, rowModel, null, labelModel);
        }

        protected DropDownItem(final String id, final IModel<T> rowModel, final IconType iconType, final IModel<String> labelModel) {
            super(id, rowModel);

            this.rowModel = rowModel;

            if (null == iconType) {
                setBody(labelModel);
            } else {
                setBody(Model.of("<i class=\"" + iconType.cssClassName() + "\"></i> " + labelModel.getObject())).setEscapeModelStrings(false);
            }
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
            DropDownItem.this.onClick(target, rowModel);
        }

        protected abstract void onClick(final AjaxRequestTarget target, final IModel<T> rowModel);
    }
}
