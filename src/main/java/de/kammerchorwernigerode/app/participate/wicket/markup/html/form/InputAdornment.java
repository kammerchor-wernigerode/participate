package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.ComponentListView;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.LinkedList;
import java.util.List;

public class InputAdornment extends Panel {

    public static final String CHILD_WICKET_ID = "child";

    private final List<Component> children = new LinkedList<>();

    public InputAdornment(String id) {
        super(id);
    }

    public InputAdornment addChild(SerializableFunction<String, Component> constructor) {
        Component child = constructor.apply(CHILD_WICKET_ID);
        return addChild(child);
    }

    public InputAdornment addChild(Component child) {
        if (!CHILD_WICKET_ID.equals(child.getId())) {
            throw new IllegalArgumentException("Invalid child Wicket ID. Must be '" + CHILD_WICKET_ID + "'.");
        }

        children.add(child);
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ComponentListView("children", children));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisible(!children.isEmpty());
    }
}
