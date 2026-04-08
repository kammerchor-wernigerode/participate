package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.IAjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.repeater.AbstractRepeater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BootstrapPagingNavigator extends AjaxPagingNavigator {

    private final IPageable pageable;

    public BootstrapPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
        this.pageable = pageable;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new PagingItem("firstItem", "first"));
        add(new PagingItem("prevItem", "prev"));
        add(new PagingItem("nextItem", "next"));
        add(new PagingItem("lastItem", "last"));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("class", "pagination mb-0");
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new BootstrapPagingNavigationIncrementLink(id, pageable, increment);
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new BootstrapPagingNavigationLink(id, pageable, pageNumber);
    }

    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        BootstrapPagingNavigation navigation = new BootstrapPagingNavigation(id, pageable, labelProvider);
        navigation.setViewSize(5);
        return navigation;
    }

    @Override
    protected void onAjaxEvent(AjaxRequestTarget target) {
        Component container = ((Component) pageable);
        while (container instanceof AbstractRepeater) {
            container = container.getParent();
        }
        target.add(container);

        if (!((MarkupContainer) container).contains(this, true)) {
            target.add(this);
        }
    }


    private static class PagingItem extends TransparentWebMarkupContainer {

        private final String childId;

        public PagingItem(String id, String childId) {
            super(id);
            this.childId = childId;
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);

            List<String> classes = Optional.ofNullable(tag.getAttribute("class"))
                .map(value -> value.split("\\s+"))
                .map(Arrays::asList)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
            classes.add("page-item");

            if (!getParent().get(childId).isEnabled()) {
                classes.add("disabled");
            }

            String cssClassNames = String.join(" ", classes);
            tag.put("class", cssClassNames);
        }
    }

    private static class BootstrapPagingNavigation extends AjaxPagingNavigation {

        private final AttributeModifier activeAttribute = AttributeModifier.append("class", "active");

        public BootstrapPagingNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
            super(id, pageable, labelProvider);
        }

        @Override
        protected void populateItem(LoopItem loopItem) {
            super.populateItem(loopItem);

            if ((getStartIndex() + loopItem.getIndex()) == pageable.getCurrentPage()) {
                loopItem.add(activeAttribute);
            }
        }

        @Override
        protected Link<?> newPagingNavigationLink(String id, IPageable pageable, long pageIndex) {
            Link<?> pagingNavigationLink = new BootstrapPagingNavigationLink(id, pageable, pageIndex);
            pagingNavigationLink.add(ClassAttributeModifier.append("class", "page-link"));
            return pagingNavigationLink;
        }
    }

    private static class BootstrapPagingNavigationIncrementLink extends AjaxPagingNavigationIncrementLink {

        public BootstrapPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
            super(id, pageable, increment);
        }

        @Override
        protected AjaxPagingNavigationBehavior newAjaxPagingNavigationBehavior(IPageable pageable, String event) {
            return new BootstrapAjaxPagingNavigationBehavior(this, pageable, event);
        }
    }

    private static class BootstrapPagingNavigationLink extends AjaxPagingNavigationLink {

        public BootstrapPagingNavigationLink(String id, IPageable pageable, long pageNumber) {
            super(id, pageable, pageNumber);
        }

        @Override
        protected AjaxPagingNavigationBehavior newAjaxPagingNavigationBehavior(IPageable pageable, String event) {
            return new BootstrapAjaxPagingNavigationBehavior(this, pageable, event);
        }
    }

    private static class BootstrapAjaxPagingNavigationBehavior extends AjaxPagingNavigationBehavior {

        private final IAjaxLink owner;

        public BootstrapAjaxPagingNavigationBehavior(IAjaxLink owner, IPageable pageable, String event) {
            super(owner, pageable, event);
            this.owner = owner;
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);
            attributes.setPreventDefault(true);
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            owner.onClick(target);

            BootstrapPagingNavigator navigator = ((Component) owner).findParent(BootstrapPagingNavigator.class);
            if (null != navigator) {
                navigator.onAjaxEvent(target);
            }
        }
    }
}
