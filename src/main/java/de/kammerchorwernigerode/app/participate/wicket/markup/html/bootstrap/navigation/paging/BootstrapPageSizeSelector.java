package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging.BootstrapPageSizeSelector.Option;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public class BootstrapPageSizeSelector extends GenericPanel<Option> {

    private final DataTable<?, ?> table;

    private final Option initialPageSize;
    private final IModel<List<Option>> pageSizes;

    public BootstrapPageSizeSelector(String id, DataTable<?, ?> table) {
        super(id, new CompoundPropertyModel<>(new Option(table.getItemsPerPage())));

        this.table = table;
        this.initialPageSize = new Option(table.getItemsPerPage());
        this.pageSizes = new PageSizesModel(table.getDataProvider(), initialPageSize);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        table.setOutputMarkupId(true);

        Select<Option> select = new Select<>("select", getModel());
        select.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                IModel<Option> model = getModel();
                Option pageSize = model.getObject();

                table.setItemsPerPage(pageSize.getValue());
                table.setCurrentPage(0);
                model.setObject(pageSize);
                target.add(table);
                onPageSizeChanged();
            }
        });
        add(select);

        SelectOptions<Option> options = new SelectOptions<>("pageSizes", pageSizes, new OptionRenderer());
        select.add(options);

        Label label = new Label("label", new ResourceModel("PageSizeSelector.label")) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                tag.put("for", select.getMarkupId());
            }
        };
        add(label);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        IModel<Option> model = getModel();

        Option current = new Option(table.getItemsPerPage());
        modelChanging();
        model.setObject(current);

        setVisible(table.getRowCount() != 0);

        List<Option> options = pageSizes.getObject();
        if (!options.contains(current)) {
            model.setObject(options.isEmpty() ? initialPageSize : options.getLast());
        }
        modelChanged();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        pageSizes.detach();
    }

    protected void onPageSizeChanged() {
    }


    private class OptionRenderer implements IOptionRenderer<Option> {

        @Override
        public String getDisplayValue(Option option) {
            return getString(option.getResourceKey(), null, String.valueOf(option.getValue()));
        }

        @Override
        public IModel<Option> getModel(Option option) {
            return new OptionModel(option);
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
                .filter(option -> option.getValue() <= totalSize)
                .toList();
        }
    }

    private static class OptionModel extends Model<Option> {

        public OptionModel(Option option) {
            super(option);
        }

        @Override
        public int hashCode() {
            Option option = getObject();
            return Objects.hash(option.getValue());
        }
    }

    @Data
    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Option implements Comparable<Option>, Serializable {

        private final String resourceKey;
        @EqualsAndHashCode.Include
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
