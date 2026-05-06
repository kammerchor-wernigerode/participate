package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDto;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.CheckBoxBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.DropDownChoiceBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.TextFieldBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.util.string.Strings;

import java.util.List;

public class AttendeeDataGrid extends Panel {

    private final DataView<AttendeeDetailsEntry> dataView;

    protected AttendeeDataGrid(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
        super(id);
        this.dataView = new AttendeeDataView("rows", dataProvider);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(dataView);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        Attributes.addClass(tag, "datagrid");
        tag.put("role", "table");
        tag.put("aria-rowcount", dataView.size());
    }


    private static class AttendeeDataView extends DataView<AttendeeDetailsEntry> {

        protected AttendeeDataView(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<AttendeeDetailsEntry> item) {
            AttendeeDetailsEntry entry = item.getModelObject();
            AttendeeDto attendeeDto = createAttendeeDto(entry);
            IModel<AttendeeDto> model = new CompoundPropertyModel<>(attendeeDto);
            Form<AttendeeDto> form = new Form<>("form", model);
            form.add(AttributeModifier.replace("aria-rowindex", item.getIndex()));
            item.add(form);


            IModel<String> commentModel = LambdaModel.of(model,
                AttendeeDto::getComment, AttendeeDto::setComment);
            WebMarkupContainer commentCell = new WebMarkupContainer("commentCell");
            commentCell.add(new TooltipBehavior(commentModel));
            form.add(commentCell);

            TextField<String> commentTextField = new TextField<>("comment", commentModel);
            commentTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            commentCell.add(commentTextField);


            IModel<Boolean> accommodationNeededModel = LambdaModel.of(model,
                AttendeeDto::isAccommodationNeeded, AttendeeDto::setAccommodationNeeded);
            WebMarkupContainer accommodationNeededCell = new WebMarkupContainer("accommodationNeededCell");
            form.add(accommodationNeededCell);

            CheckBox accommodationNeededCheckBox = new CheckBox("accommodationNeeded", accommodationNeededModel);
            accommodationNeededCheckBox.add(new CheckBoxBehavior());
            accommodationNeededCell.add(accommodationNeededCheckBox);


            IModel<Integer> bedsOfferedCountModel = LambdaModel.of(model,
                AttendeeDto::getBedsOfferedCount, AttendeeDto::setBedsOfferedCount);
            NumberTextField<Integer> bedsOfferedCountNumberTextField = new NumberTextField<>("bedsOfferedCount",
                bedsOfferedCountModel, Integer.class);
            bedsOfferedCountNumberTextField.setMinimum(0);
            bedsOfferedCountNumberTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            form.add(bedsOfferedCountNumberTextField);


            IModel<Boolean> byCarModel = LambdaModel.of(model,
                AttendeeDto::isByCar, AttendeeDto::setByCar);
            WebMarkupContainer byCarCell = new WebMarkupContainer("byCarCell");
            form.add(byCarCell);

            CheckBox byCarCheckBox = new CheckBox("byCar", byCarModel);
            byCarCheckBox.add(new CheckBoxBehavior());
            byCarCell.add(byCarCheckBox);


            IModel<Integer> carSeatCountModel = LambdaModel.of(model,
                AttendeeDto::getCarSeatCount, AttendeeDto::setCarSeatCount);
            NumberTextField<Integer> carSeatCountNumberTextField = new NumberTextField<>("carSeatCount",
                carSeatCountModel, Integer.class);
            carSeatCountNumberTextField.setMinimum(0);
            carSeatCountNumberTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            form.add(carSeatCountNumberTextField);


            IModel<InvitationStatus> invitationStatusModel = LambdaModel.of(model,
                AttendeeDto::getInvitationStatus, AttendeeDto::setInvitationStatus);
            WebMarkupContainer invitationStatusCell = new WebMarkupContainer("invitationStatusCell");
            form.add(invitationStatusCell);

            DropDownChoice<InvitationStatus> invitationStatusDropDownChoice = new DropDownChoice<>("invitationStatus",
                invitationStatusModel, AttendeeDataView::supplyPresenceChoices, new EnumChoiceRenderer<>());
            invitationStatusDropDownChoice.add(new DropDownChoiceBehavior(DropDownChoiceBehavior.Size.SMALL));
            invitationStatusCell.add(invitationStatusDropDownChoice);


            Label nameCell = new Label("name", item.getModel().map(this::printName));
            form.add(nameCell);
        }

        private AttendeeDto createAttendeeDto(AttendeeDetailsEntry entry) {
            AttendeeDto dto = new AttendeeDto(entry.getId());
            dto.setInvitationStatus(entry.getInvitationStatus());
            dto.setAccommodationNeeded(entry.isAccommodationNeeded());
            dto.setBedsOfferedCount(entry.getBedsOfferedCount());
            dto.setByCar(entry.isByCar());
            dto.setCarSeatCount(entry.getCarSeatCount());
            dto.setComment(entry.getComment());
            return dto;
        }

        private static List<InvitationStatus> supplyPresenceChoices() {
            InvitationStatus[] values = InvitationStatus.values();
            return List.of(values);
        }

        private String printName(AttendeeDetailsEntry entry) {
            String fileName = entry.getFileName();
            if (!Strings.isEmpty(fileName)) {
                return fileName;
            }

            String lastName = entry.getLastName();
            String firstName = entry.getFirstName();
            return firstName + " " + lastName;
        }
    }
}
