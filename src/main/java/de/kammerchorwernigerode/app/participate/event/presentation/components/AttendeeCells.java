package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import lombok.experimental.UtilityClass;

@UtilityClass
class AttendeeCells {

    public static void decorateStatus(Component component, IModel<InvitationStatus> rowModel) {
        IModel<String> cellCssClassNames = rowModel.map(AttendeeCells::cssClassNames);
        component.add(ClassAttributeModifier.append("class", cellCssClassNames));
    }

    public static void decorateSelection(Component component, IModel<Boolean> rowModel) {
        IModel<String> cellCssClassNames = rowModel.map(AttendeeCells::cssClassNames);
        component.add(ClassAttributeModifier.append("class", cellCssClassNames));
    }

    public static void decorateIncrement(Component component, IModel<? extends Number> rowModel) {
        IModel<String> cellCssClassNames = rowModel.map(AttendeeCells::cssClassNames);
        component.add(ClassAttributeModifier.append("class", cellCssClassNames));
    }

    private static String cssClassNames(InvitationStatus invitationStatus) {
        return switch (invitationStatus) {
            case TENTATIVE -> "bg-warning-subtle";
            case ACCEPTED -> "bg-success-subtle";
            case DECLINED -> "bg-danger-subtle";
            case UNINVITED, PENDING -> null;
        };
    }

    private static String cssClassNames(Boolean selected) {
        return selected
            ? "bg-success-subtle"
            : null;
    }

    private static String cssClassNames(Number number) {
        if (null == number || number.doubleValue() == 0) {
            return null;
        } else {
            return "bg-success-subtle";
        }
    }
}
