package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDto;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.wicket.behavior.FocusTrackingBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.CheckBoxBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.DropDownChoiceBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.TextFieldBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
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
import java.util.Objects;

public class AttendeeDataGrid extends Panel {

    private final DataView<AttendeeDetailsEntry> dataView;

    protected AttendeeDataGrid(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
        super(id);
        this.dataView = new AttendeeDataView("rows", dataProvider);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(dataView);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        Attributes.addClass(tag, "datagrid");
        tag.put("role", "table");
        tag.put("aria-rowcount", dataView.size());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        String markupId = getMarkupId();
        response.render(CssContentHeaderItem.forCSS("""
                #%s .datagrid-group-start > .datagrid-cell {
                    border-top: 3px solid var(--bs-border-color) !important;
                }\
                """.formatted(markupId),
            markupId + "-styles"));
    }


    private static class AttendeeDataView extends DataView<AttendeeDetailsEntry> {

        private Voice prevVoice;
        private Integer prevInvitationStatusOrder;
        private boolean startNewGroup;

        protected AttendeeDataView(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void onBeforeRender() {
            prevVoice = null;
            prevInvitationStatusOrder = null;
            startNewGroup = false;
            super.onBeforeRender();
        }

        @Override
        protected void populateItem(Item<AttendeeDetailsEntry> item) {
            onPopulate(item);

            AttendeeDetailsEntry entry = item.getModelObject();
            AttendeeDto attendeeDto = createAttendeeDto(entry);
            IModel<AttendeeDto> model = new CompoundPropertyModel<>(attendeeDto);
            Form<AttendeeDto> form = new Form<>("form", model);
            form.add(ClassAttributeModifier.append("class", startNewGroup ? "datagrid-group-start" : null));
            form.add(AttributeModifier.replace("aria-rowindex", item.getIndex()));
            item.add(form);


            IModel<String> commentModel = LambdaModel.of(model,
                AttendeeDto::getComment, AttendeeDto::setComment);
            WebMarkupContainer commentCell = new WebMarkupContainer("commentCell");
            commentCell.add(new TooltipBehavior(commentModel));
            form.add(commentCell);

            TextField<String> commentTextField = new TextField<>("comment", commentModel);
            commentTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            commentTextField.add(new FocusTrackingBehavior());
            commentCell.add(commentTextField);


            IModel<Boolean> accommodationNeededModel = LambdaModel.of(model,
                AttendeeDto::isAccommodationNeeded, AttendeeDto::setAccommodationNeeded);
            WebMarkupContainer accommodationNeededCell = new WebMarkupContainer("accommodationNeededCell");
            AttendeeCells.decorateSelection(accommodationNeededCell, accommodationNeededModel);
            form.add(accommodationNeededCell);

            CheckBox accommodationNeededCheckBox = new CheckBox("accommodationNeeded", accommodationNeededModel);
            accommodationNeededCheckBox.add(new CheckBoxBehavior());
            accommodationNeededCheckBox.add(new FocusTrackingBehavior());
            accommodationNeededCell.add(accommodationNeededCheckBox);


            IModel<Integer> bedsOfferedCountModel = LambdaModel.of(model,
                AttendeeDto::getBedsOfferedCount, AttendeeDto::setBedsOfferedCount);
            NumberTextField<Integer> bedsOfferedCountNumberTextField = new NumberTextField<>("bedsOfferedCount",
                bedsOfferedCountModel, Integer.class);
            bedsOfferedCountNumberTextField.setMinimum(0);
            bedsOfferedCountNumberTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            bedsOfferedCountNumberTextField.add(new FocusTrackingBehavior());
            AttendeeCells.decorateIncrement(bedsOfferedCountNumberTextField, bedsOfferedCountModel);
            form.add(bedsOfferedCountNumberTextField);


            IModel<Boolean> byCarModel = LambdaModel.of(model,
                AttendeeDto::isByCar, AttendeeDto::setByCar);
            WebMarkupContainer byCarCell = new WebMarkupContainer("byCarCell");
            AttendeeCells.decorateSelection(byCarCell, byCarModel);
            form.add(byCarCell);

            CheckBox byCarCheckBox = new CheckBox("byCar", byCarModel);
            byCarCheckBox.add(new CheckBoxBehavior());
            byCarCheckBox.add(new FocusTrackingBehavior());
            byCarCell.add(byCarCheckBox);


            IModel<Integer> carSeatCountModel = LambdaModel.of(model,
                AttendeeDto::getCarSeatCount, AttendeeDto::setCarSeatCount);
            NumberTextField<Integer> carSeatCountNumberTextField = new NumberTextField<>("carSeatCount",
                carSeatCountModel, Integer.class);
            carSeatCountNumberTextField.setMinimum(0);
            carSeatCountNumberTextField.add(new TextFieldBehavior(TextFieldBehavior.Size.SMALL));
            carSeatCountNumberTextField.add(new FocusTrackingBehavior());
            AttendeeCells.decorateIncrement(carSeatCountNumberTextField, carSeatCountModel);
            form.add(carSeatCountNumberTextField);


            IModel<InvitationStatus> invitationStatusModel = LambdaModel.of(model,
                AttendeeDto::getInvitationStatus, AttendeeDto::setInvitationStatus);
            WebMarkupContainer invitationStatusCell = new WebMarkupContainer("invitationStatusCell");
            AttendeeCells.decorateStatus(invitationStatusCell, invitationStatusModel);
            form.add(invitationStatusCell);

            DropDownChoice<InvitationStatus> invitationStatusDropDownChoice = new DropDownChoice<>("invitationStatus",
                invitationStatusModel, AttendeeDataView::supplyPresenceChoices, new EnumChoiceRenderer<>());
            invitationStatusDropDownChoice.add(new DropDownChoiceBehavior(DropDownChoiceBehavior.Size.SMALL));
            invitationStatusDropDownChoice.add(new FocusTrackingBehavior());
            AttendeeCells.decorateStatus(invitationStatusDropDownChoice, invitationStatusModel);
            invitationStatusCell.add(invitationStatusDropDownChoice);


            Label nameCell = new Label("name", item.getModel().map(this::printName));
            AttendeeCells.decorateStatus(nameCell, invitationStatusModel);
            form.add(nameCell);


            form.visitFormComponents((formComponent, ignored) -> {
                formComponent.add(new AjaxFormSubmitBehavior("change") {

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        super.onEvent(target);

                        if ("invitationStatus".equals(formComponent.getId())) {
                            AttendeeDataGrid dataGrid = findParent(AttendeeDataGrid.class);
                            target.add(dataGrid);
                        } else {
                            target.add(form);
                        }
                    }
                });
            });
        }

        protected void onPopulate(Item<AttendeeDetailsEntry> item) {
            AttendeeDetailsEntry entry = item.getModelObject();
            int index = item.getIndex();
            Voice voice = entry.getVoice();
            int invitationStatusOrder = entry.getInvitationStatusOrder();

            boolean isFirstRow = index == 0;
            boolean orderBoundaryFromZeroToOne =
                prevInvitationStatusOrder != null
                    && prevInvitationStatusOrder == 0
                    && invitationStatusOrder == 1;
            boolean voiceChangedInZeroBlock =
                invitationStatusOrder == 0
                    && prevInvitationStatusOrder != null
                    && prevInvitationStatusOrder == 0
                    && !Objects.equals(prevVoice, voice);

            startNewGroup = isFirstRow || voiceChangedInZeroBlock || orderBoundaryFromZeroToOne;
            prevVoice = voice;
            prevInvitationStatusOrder = invitationStatusOrder;
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
