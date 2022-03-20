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

/**
 * Icon panel, to provide an icon for eg. table columns.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class IconPanel extends Panel {

    /**
     * {@link IconType}
     */
    private IconType type;

    /**
     * {@link Color}
     */
    private Color color;

    /**
     * {@link de.vinado.wicket.participate.components.TextAlign}
     */
    private TextAlign textAlign;

    /**
     * {@link Display}
     */
    private Display display;

    /**
     * Css class
     */
    private IModel<String> cssClassNameModel;

    /**
     * Construct.
     *
     * @param id Wicket ID.
     */
    public IconPanel(String id) {
        this(id, FontAwesome5IconType.code_s, Color.DEFAULT, TextAlign.LEFT);
    }

    /**
     * @param id   Wicket ID
     * @param icon {@link IconType}
     */
    public IconPanel(String id, IconType icon) {
        this(id, icon, Color.DEFAULT, TextAlign.LEFT);
    }

    /**
     * Construct.
     *
     * @param id        Wicket ID
     * @param type      {@link IconType}
     * @param color     {@link Color}
     * @param textAlign {@link TextAlign}
     */
    public IconPanel(String id, IconType type, Color color, TextAlign textAlign) {
        super(id);
        this.type = type;
        this.color = color;
        this.textAlign = textAlign;
        this.display = Display.DEFAULT;

        cssClassNameModel = Model.of(type.cssClassName());
        final Label label = new Label("label", Model.of(""));
        label.add(new CssClassNameAppender(cssClassNameModel));
        label.setOutputMarkupId(true);
        add(label);
    }

    /**
     * Returns the {@link IconType} of the panel
     *
     * @return IconType
     */
    public IconType getType() {
        return type;
    }

    /**
     * Sets the {@link IconType}
     *
     * @param type IconType
     * @return {@link IconPanel}
     */
    public IconPanel setType(final IconType type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the {@link Color} of the panel.
     *
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the {@link Color} and returns this object
     *
     * @param color Color
     * @return {@link IconPanel}
     */
    public IconPanel setColor(final Color color) {
        this.color = color;
        return this;
    }

    /**
     * Returns the {@link TextAlign} of the panel icon
     *
     * @return TextAlign
     */
    public TextAlign getTextAlign() {
        return textAlign;
    }

    /**
     * Sets the {@link TextAlign} of the icon panel
     *
     * @param textAlign {@link TextAlign}
     * @return {@link IconPanel}
     */
    public IconPanel setTextAlign(final TextAlign textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    /**
     * Returns the {@link Display} style of the panel icon
     *
     * @return TextAlign
     */
    public Display getDisplay() {
        return display;
    }

    /**
     * Sets the {@link Display} style of the icon panel
     *
     * @param display {@link Display}
     * @return {@link IconPanel}
     */
    public IconPanel setDisplay(final Display display) {
        this.display = display;
        return this;
    }

    /**
     * Puts the configured attribute, like {@link TextAlign} onto the {@link org.apache.wicket.Component}
     *
     * @param tag {@link ComponentTag}
     */
    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);
        final CssClassNames.Builder builder = CssClassNames.newBuilder();
        builder.add(textAlign.cssClassName());
        builder.add(color.cssClassName());
        builder.add(display.cssClassName());
        Attributes.addClass(tag, builder.asString());
    }

    /**
     * Refreshes the {@link IconPanel} on configure.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        cssClassNameModel.setObject(type.cssClassName());
    }

    /**
     * Icon color
     */
    public enum Color implements ICssClassNameProvider {
        DEFAULT(""),
        MUTED("text-muted"),
        PRIMARY("text-primary"),
        SUCCESS("text-success"),
        INFO("text-info"),
        WARNING("text-warning"),
        DANGER("text-danger");

        /**
         * Css class name
         */
        private final String cssClassName;

        /**
         * Construct.
         *
         * @param cssClassName Css class name
         */
        Color(final String cssClassName) {
            this.cssClassName = cssClassName;
        }

        /**
         * @return class
         * @see #toString()
         */
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

        Display(final String cssClassName) {
            this.cssClassName = cssClassName;
        }

        @Override
        public String cssClassName() {
            return cssClassName;
        }
    }
}

