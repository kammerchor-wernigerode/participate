package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.core.util.CssClassNames;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.components.TextAlign;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class IconPanel extends Panel {

    private IconType type;

    private Color color;

    private TextAlign textAlign;

    private Display display;

    private IModel<String> cssClassNameModel;

    public IconPanel(String id) {
        this(id, FontAwesome5IconType.code_s, Color.DEFAULT, TextAlign.START);
    }

    public IconPanel(String id, IconType type, Color color, TextAlign textAlign) {
        super(id);
        this.type = type;
        this.color = color;
        this.textAlign = textAlign;
        this.display = Display.DEFAULT;

        cssClassNameModel = Model.of(type.cssClassName());
        Label label = new Label("label", Model.of(""));
        label.add(new CssClassNameAppender(cssClassNameModel));
        label.setOutputMarkupId(true);
        add(label);
    }

    public IconType getType() {
        return type;
    }

    public IconPanel setType(IconType type) {
        this.type = type;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public IconPanel setColor(Color color) {
        this.color = color;
        return this;
    }

    public IconPanel setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    public Display getDisplay() {
        return display;
    }

    public IconPanel setDisplay(Display display) {
        this.display = display;
        return this;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        CssClassNames.Builder builder = CssClassNames.newBuilder();
        builder.add(textAlign.cssClassName());
        builder.add(color.cssClassName());
        builder.add(display.cssClassName());
        Attributes.addClass(tag, builder.asString());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        cssClassNameModel.setObject(type.cssClassName());
    }

    public enum Color implements ICssClassNameProvider {
        DEFAULT(""),
        MUTED("text-muted"),
        PRIMARY("text-primary"),
        SUCCESS("text-success"),
        INFO("text-info"),
        WARNING("text-warning"),
        DANGER("text-danger");

        private final String cssClassName;

        Color(String cssClassName) {
            this.cssClassName = cssClassName;
        }

        @Override
        public String cssClassName() {
            return cssClassName;
        }
    }

    public enum Display implements ICssClassNameProvider {
        DEFAULT(""),
        BLOCK("d-block"),
        INLINE("d-inline"),
        INLINE_BLOCK("d-inline-block");

        private final String cssClassName;

        Display(String cssClassName) {
            this.cssClassName = cssClassName;
        }

        @Override
        public String cssClassName() {
            return cssClassName;
        }
    }
}
