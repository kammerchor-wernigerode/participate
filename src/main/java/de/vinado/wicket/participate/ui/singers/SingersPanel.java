package de.vinado.wicket.participate.ui.singers;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.wicket.bt5.modal.Modal;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.events.SingerUpdateEvent;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.List;

public class SingersPanel extends BootstrapPanel<SingerFilter> {

    private static final int ROWS_PER_PAGE = 20;

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    public final Modal modal;

    public SingersPanel(String id, IModel<SingerFilter> model) {
        super(id, model);

        this.modal = modal("modal");
    }

    protected Modal modal(String wicketId) {
        return new Modal(wicketId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(modal);

        addQuickAccessAction(AjaxAction.create(new ResourceModel("singer.add", "Add Singer"),
            FontAwesome6IconType.plus_s,
            this::add));

        add(filter());
        add(table());
    }

    private void add(AjaxRequestTarget target) {
        IModel<SingerDTO> model = new CompoundPropertyModel<>(new SingerDTO());

        modal
            .title(new ResourceModel("singer.add", "Add Singer"))
            .content(id -> new AddEditSingerPanel(id, model))
            .addCloseAction(new ResourceModel("cancel", "Cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), this::onAdd)
            .show(target);
    }

    private void onAdd(AjaxRequestTarget target) {
        broadcastSingerUpdateEvent(target);
    }

    private SingerFilterForm filter() {
        return new SingerFilterForm("filter", getModel()) {
            @Override
            protected void onApply() {
                send(SingersPanel.this, Broadcast.BREADTH, new SingerFilterIntent(getModelObject()));
            }
        };
    }

    private Component table() {
        SortableDataProvider<Singer, SerializableFunction<Singer, ?>> dataProvider = dataProvider();
        dataProvider.setSort(with(Singer::getSortName), SortOrder.ASCENDING);
        return new BootstrapAjaxDataTable<>("dataTable", columns(), dataProvider, ROWS_PER_PAGE)
            .condensed().hover()
            .setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance())
            .setOutputMarkupId(true)
            .add(new CssClassNameAppender("singers"));
    }

    private SortableDataProvider<Singer, SerializableFunction<Singer, ?>> dataProvider() {
        return new SingerDataProvider(getModel(), personService);
    }

    private List<IColumn<Singer, SerializableFunction<Singer, ?>>> columns() {
        return Arrays.asList(
            nameColumn(),
            emailAddressColumn(),
            voiceColumn(),
            editColumn()
        );
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> nameColumn() {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), with(Singer::getSortName), "sortName") {
            @Override
            public String getCssClass() {
                return "name";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> emailAddressColumn() {
        return new PropertyColumn<>(new ResourceModel("email", "Email"), with(Singer::getEmail), "email") {
            @Override
            public void populateItem(Item<ICellPopulator<Singer>> item, String componentId, IModel<Singer> rowModel) {
                item.add(new SmartLinkLabel(componentId, getDataModel(rowModel).map(String.class::cast))
                    .add(new CssClassNameAppender("nobusy")));
            }

            @Override
            public String getCssClass() {
                return "emailAddress";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> voiceColumn() {
        return new EnumColumn<Singer, SerializableFunction<Singer, ?>, Voice>(new ResourceModel("voice", "voice"),
            with(Singer::getVoice), "voice") {
            @Override
            public String getCssClass() {
                return "voice";
            }
        };
    }

    private IColumn<Singer, SerializableFunction<Singer, ?>> editColumn() {
        return new BootstrapAjaxLinkColumn<>(FontAwesome6IconType.pencil_s,
            new ResourceModel("singer.edit", "Edit Singer")) {
            @Override
            public void onClick(AjaxRequestTarget target, IModel<Singer> rowModel) {
                Singer singer = rowModel.getObject();
                CompoundPropertyModel<SingerDTO> model = new CompoundPropertyModel<>(new SingerDTO(singer));

                modal
                    .title(new ResourceModel("singer.edit", "Edit Singer"))
                    .content(id -> new AddEditSingerPanel(id, model))
                    .addCloseAction(new ResourceModel("cancel", "Cancel"))
                    .addSubmitAction(new ResourceModel("save", "Save"), onUpdate(model))
                    .show(target);
            }

            private SerializableConsumer<AjaxRequestTarget> onUpdate(CompoundPropertyModel<SingerDTO> model) {
                return target -> {
                    Singer singer = model.map(SingerDTO::getSinger).getObject();
                    if (null == singer || !singer.isActive()) {
                        Snackbar.show(target, new ResourceModel("singer.remove.success", "The singer has been removed"));
                    }

                    broadcastSingerUpdateEvent(target);
                };
            }

            @Override
            public String getCssClass() {
                return "edit";
            }
        };
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }

    private void broadcastSingerUpdateEvent(AjaxRequestTarget target) {
        send(getWebPage(), Broadcast.BREADTH, new SingerUpdateEvent(target));
    }

    @Override
    protected IModel<String> titleModel() {
        return new ResourceModel("singers", "Singers");
    }
}
