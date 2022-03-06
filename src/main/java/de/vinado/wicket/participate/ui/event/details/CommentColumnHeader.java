package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.angle_double_down_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.angle_double_up_s;
import static de.vinado.wicket.participate.components.ShortenedMultilineLabel.Intent;
import static de.vinado.wicket.participate.components.ShortenedMultilineLabel.State;
import static de.vinado.wicket.participate.components.ShortenedMultilineLabel.State.COLLAPSED;
import static de.vinado.wicket.participate.components.ShortenedMultilineLabel.State.EXPANDED;

/**
 * @author Vincent Nadoll
 */
public abstract class CommentColumnHeader extends GenericPanel<String> {

    public CommentColumnHeader(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(name("name"));
        add(commentToggle("toggle"));
    }

    private Component name(String id) {
        return new Label(id, getModel());
    }

    private Component commentToggle(String id) {
        return new CommentToggle(id);
    }


    private final class CommentToggle extends Panel {

        public CommentToggle(String id) {
            super(id);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(expandButton("expand"));
            add(collapseButton("collapse"));
        }

        private Component expandButton(String id) {
            return button(id, EXPANDED, COLLAPSED, angle_double_down_s);
        }

        private Component collapseButton(String id) {
            return button(id, COLLAPSED, EXPANDED, angle_double_up_s);
        }

        private WebMarkupContainer button(String id, State targetState, State represented, IconType icon) {
            BootstrapAjaxLink<Intent> button = new BootstrapAjaxLink<Intent>(id, Buttons.Type.Link) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    send(CommentColumnHeader.this.getCommentToggleScope(), Broadcast.BREADTH, Intent.ensure(targetState));
                }
            }
                .setIconType(icon)
                .setSize(Buttons.Size.Small);
            button.add(new TooltipBehavior(getTooltipResourceKey(represented)));
            return button;
        }

        private IModel<String> getTooltipResourceKey(State represented) {
            String state = represented.name().toLowerCase();
            return new ResourceModel("event.details.participant.comment.toggle.all." + state + ".tooltip");
        }
    }

    protected abstract Component getCommentToggleScope();
}
