package de.vinado.wicket.participate.ui.member.groups;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.panel.BootstrapAjaxLinkPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class GroupPanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private IModel<Group> model;

    private SimpleDataProvider<Member, String> dataProvider;
    private BootstrapAjaxDataTable<Member, String> dataTable;

    public GroupPanel(final String id, final IModel<Group> model) {
        super(id, model);

        this.model = model;

        dataProvider = new SimpleDataProvider<Member, String>(personService.getGroupMemberList(model.getObject())) {
            @Override
            public String getDefaultSort() {
                return "person.sortName";
            }
        };

        final List<IColumn<Member, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "person.sortName", "person.sortName"));
        columns.add(new PropertyColumn<>(new ResourceModel("voiceGroup", "Voice"), "voice.sortOrder", "voice.name"));
        columns.add(new AbstractColumn<Member, String>(Model.of()) {
            @Override
            public void populateItem(final Item<ICellPopulator<Member>> item, final String componentId, final IModel<Member> rowModel) {
                item.add(new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, FontAwesomeIconType.times, new ResourceModel("removeMemberToGroup", "Remove person to group")) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        personService.dissociateMemberToGroup(rowModel.getObject(), model.getObject());
                        dataProvider.set(personService.getGroupMemberList(model.getObject()));
                        target.add(dataTable);
                        Snackbar.show(target, new ResourceModel("removeMemberToGroupA", "Removed person to group"));
                    }
                });
            }

            @Override
            public String getCssClass() {
                return "width-fix-30";
            }
        });

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 15);
        dataTable.hover().condensed();
        dataTable.setOutputMarkupId(true);
        add(dataTable);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        final Object payload = event.getPayload();
        if (payload instanceof AjaxUpdateEvent) {
            final AjaxUpdateEvent updateEvent = (AjaxUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            dataProvider.set(personService.getGroupMemberList(model.getObject()));
            target.add(dataTable);
        }
    }
}
