package de.vinado.wicket.participate.component.behavoir;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapFormVisitor implements IVisitor<Component, Void>, Serializable {

    private final Set<FormComponent> visited = new HashSet<>();

    private boolean found = false;

    @Override
    public void component(final Component c, final IVisit<Void> visit) {
        if (!visited.contains(c) && c instanceof FormComponent && !(c instanceof Button)) {
            final FormComponent fc = (FormComponent) c;
            visited.add(fc);
            if (!found && fc.isEnabled() && fc.isVisible() && (fc instanceof DropDownChoice || fc instanceof AbstractTextComponent)) {
                found = true;
                fc.add(new FocusBehavior());
                visit.stop();
            }
        }
    }
}
