package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class BootstrapPageSizeSelector extends GenericPanel<BootstrapPageSizeSelector.Option> {

    private final Option initialPageSize;
    private final DataTable<?, ?> table;

    public BootstrapPageSizeSelector(String id, DataTable<?, ?> table) {
        super(id, new CompoundPropertyModel<>(new Option(table.getItemsPerPage())));
        this.initialPageSize = new Option(table.getItemsPerPage());
        this.table = table;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        SelectorForm form = new SelectorForm("form", getModel());
        add(form);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        long currentPageSize = table.getItemsPerPage();
        setModelObject(new Option(currentPageSize));
    }

    protected void onPageSizeChanged() {
    }


    private class SelectorForm extends Form<Option> {

        private final IModel<List<Option>> pageSizes;

        public SelectorForm(String id, IModel<Option> model) {
            super(id, model);
            this.pageSizes = new PageSizesModel(table.getDataProvider(), initialPageSize);
        }

        @Override
        protected void onSubmit() {
            Option pageSize = getModelObject();

            table.setItemsPerPage(pageSize.value);
            table.setCurrentPage(0);

            onPageSizeChanged();
            RequestCycle.get().find(AjaxRequestTarget.class)
                .ifPresent(target -> target.add(table));
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            table.setOutputMarkupId(true);

            IChoiceRenderer<Option> renderer = new LambdaChoiceRenderer<>(option -> getString(option.resourceKey, null,
                String.valueOf(option.value)), Option::getValue);
            DropDownChoice<Option> select = new DropDownChoice<>("select", getModel(), pageSizes, renderer);
            select.add(new PageSizeSelectBehavior());
            select.setLabel(new ResourceModel("PageSizeSelector.label"));
            add(select);

            SimpleFormComponentLabel label = new SimpleFormComponentLabel("label", select);
            add(label);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            List<Option> options = pageSizes.getObject();
            if (options.contains(getModelObject())) {
                return;
            }

            setModelObject(options.isEmpty() ? initialPageSize : options.getLast());
        }

        @Override
        protected void detachModel() {
            super.detachModel();
            pageSizes.detach();
        }


        private static class PageSizeSelectBehavior extends AjaxFormSubmitBehavior {

            public PageSizeSelectBehavior() {
                super("change");
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setPreventDefault(true);
            }
        }
    }


    @RequiredArgsConstructor
    private static class PageSizesModel extends LoadableDetachableModel<List<Option>> {

        private static final List<Long> VALUES = List.of(5L, 10L, 25L, 50L, 100L, 250L, 500L);

        private final IDataProvider<?> dataProvider;
        private final Option initialPageSize;

        @Override
        protected List<Option> load() {
            long providerSize = dataProvider.size();
            long totalSize = Math.max(1, providerSize);

            SortedSet<Option> options = VALUES.stream()
                .map(Option::new)
                .collect(Collectors.toCollection(TreeSet::new));
            options.add(initialPageSize);
            options.add(new Option("PageSizeSelector.option.all", totalSize));

            return options.stream()
                .filter(option -> option.value <= totalSize)
                .toList();
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Option implements Comparable<Option>, Serializable {

        private final String resourceKey;
        private final long value;

        public Option(long value) {
            this(String.valueOf(value), value);
        }

        @Override
        public int compareTo(Option that) {
            return Comparator.comparing(Option::getValue)
                .compare(this, that);
        }
    }
}
