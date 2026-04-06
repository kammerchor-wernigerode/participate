package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image.Icon;
import org.apache.wicket.Application;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Classes;

public class InvitationStatusIcon extends GenericPanel<InvitationStatus> {

    public InvitationStatusIcon(String id, IModel<InvitationStatus> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        InvitationStatus invitationStatus = getModelObject();

        Icon icon = new Icon("icon", null);
        add(icon);

        switch (invitationStatus) {
            case UNINVITED:
                icon.setType(Bi.circle);
                add(ClassAttributeModifier.append("class", "text-disabled"));
                break;
            case TENTATIVE:
                icon.setType(Bi.question);
                add(ClassAttributeModifier.append("class", "text-info"));
                break;
            case ACCEPTED:
                icon.setType(Bi.check);
                add(ClassAttributeModifier.append("class", "text-success"));
                break;
            case DECLINED:
                icon.setType(Bi.x);
                add(ClassAttributeModifier.append("class", "text-danger"));
                break;
            case PENDING:
                icon.setType(Bi.circle_fill);
                add(ClassAttributeModifier.append("class", "text-warning"));
                break;
            default:
                icon.setType(null);
                break;
        }

        String key = Classes.simpleName(invitationStatus.getDeclaringClass()) + '.' + invitationStatus.name();
        String tooltip = Application.get().getResourceSettings().getLocalizer().getString(key, null);

        add(new TooltipBehavior(tooltip));
        add(ClassAttributeModifier.append("class", "text-center"));
        add(icon);
    }
}
