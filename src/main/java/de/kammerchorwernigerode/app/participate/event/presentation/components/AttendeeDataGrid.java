package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.components.AttendeeDataGrid.Event.ItemUpdated;
import de.kammerchorwernigerode.app.participate.event.presentation.components.attendee.PeriodLabel;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDto;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.wicket.behavior.FocusTrackingBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.Anchor;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.CheckBoxBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.DropDownChoiceBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.TextFieldBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalHiddenEventBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.LocalDateTimeFormControl;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.io.IClusterable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
            Form<AttendeeDto> form = new Form<>("form", model) {

                @Override
                protected void onSubmit() {
                    super.onSubmit();

                    send(getPage(), Broadcast.BREADTH, new ItemUpdated());
                }
            };
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


            WebMarkupContainer periodCell = new WebMarkupContainer("periodCell");
            AttendeeCells.decorateStatus(periodCell, invitationStatusModel);
            form.add(periodCell);

            PeriodLabel periodLabel = new PeriodLabel("periodLabel", model, item.getModel());
            periodLabel.add(new TooltipBehavior(periodLabel.printDates()));
            periodCell.add(periodLabel);

            Modal modal = new Modal("modal")
                .centered(true)
                .size(Modal.Size.SMALL)
                .title(new ResourceModel("attendee.presence"))
                .content(id -> new PeriodModalContent(id, model, item.getModel()))
                .addCloseAction(new ResourceModel("close"))
                .addSubmitAction(new ResourceModel("save"));
            modal.add(new ModalHiddenEventBehavior() {

                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    super.onEvent(target);
                    target.add(form);
                }
            });
            periodCell.add(modal);
            AjaxLink<AttendeeDto> periodLink = new AjaxLink<>("periodLink", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    modal.show(target);
                }
            };
            periodCell.add(periodLink);


            Label nameCell = new Label("name", item.getModel().map(AttendeeDetailsEntry::getDisplayName));
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
            dto.setFromDateTime(entry.getFromDateTime());
            dto.setToDateTime(entry.getToDateTime());
            return dto;
        }

        private static List<InvitationStatus> supplyPresenceChoices() {
            InvitationStatus[] values = InvitationStatus.values();
            return List.of(values);
        }


        private static class PeriodModalContent extends GenericPanel<AttendeeDto> {

            private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT);

            private final IModel<AttendeeDetailsEntry> entry;

            public PeriodModalContent(String id, IModel<AttendeeDto> model, IModel<AttendeeDetailsEntry> entry) {
                super(id, model);
                this.entry = entry;
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();

                IModel<AttendeeDto> model = getModel();

                IModel<LocalDateTime> fromModel = LambdaModel.of(model,
                    AttendeeDto::getFromDateTime, AttendeeDto::setFromDateTime);
                LocalDateTimeFormControl fromControl = new LocalDateTimeFormControl("fromDateTime", fromModel);
                fromControl.setOutputMarkupId(true);
                fromControl.setLabel(new ResourceModel("AttendeeForm.from"));
                fromControl.addEndAdornment(id -> {
                    IModel<LocalDateTime> start = entry.map(entry -> {
                        Instant instant = entry.getStartInstant();
                        ZoneId zoneId = entry.getStartZoneId();
                        return LocalDateTime.ofInstant(instant, zoneId);
                    });

                    Anchor anchor = new Anchor(id);

                    FormComponent<LocalDateTime> formComponent = fromControl.getFormComponent();
                    ResetLocalDateTimeLink link = new ResetLocalDateTimeLink(anchor.getLinkId(), start, formComponent);
                    link.setVariant(Variant.OUTLINE_SECONDARY);
                    link.add(new TooltipBehavior(start.map(PeriodModalContent.this::print)));
                    link.setIcon(Bi.arrow_counterclockwise);

                    anchor.add(link);
                    return anchor;
                });
                add(fromControl);


                IModel<LocalDateTime> toModel = LambdaModel.of(model,
                    AttendeeDto::getToDateTime, AttendeeDto::setToDateTime);
                LocalDateTimeFormControl toControl = new LocalDateTimeFormControl("toDateTime", toModel);
                toControl.setOutputMarkupId(true);
                toControl.setLabel(new ResourceModel("AttendeeForm.to"));
                toControl.addEndAdornment(id -> {
                    IModel<LocalDateTime> end = entry.map(entry -> {
                        Instant instant = entry.getEndInstant();
                        ZoneId zoneId = entry.getEndZoneId();
                        return LocalDateTime.ofInstant(instant, zoneId);
                    });

                    Anchor anchor = new Anchor(id);

                    FormComponent<LocalDateTime> formComponent = toControl.getFormComponent();
                    ResetLocalDateTimeLink link = new ResetLocalDateTimeLink(anchor.getLinkId(), end, formComponent);
                    link.setVariant(Variant.OUTLINE_SECONDARY);
                    link.add(new TooltipBehavior(end.map(PeriodModalContent.this::print)));
                    link.setIcon(Bi.arrow_counterclockwise);

                    anchor.add(link);
                    return anchor;
                });
                add(toControl);
            }

            private String print(LocalDateTime dateTime) {
                DateTimeFormatter formatter = DATE_TIME_FORMATTER.localizedBy(getLocale());
                return formatter.format(dateTime);
            }


            private static class ResetLocalDateTimeLink extends BootstrapAjaxLink<LocalDateTime> {

                private final FormComponent<LocalDateTime> formComponent;

                public ResetLocalDateTimeLink(String id, IModel<LocalDateTime> model,
                                              FormComponent<LocalDateTime> formComponent) {
                    super(id, model);
                    this.formComponent = formComponent;
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    IConverter<LocalDateTime> converter = formComponent.getConverter(LocalDateTime.class);

                    String markupId = formComponent.getMarkupId();
                    String value = converter.convertToString(getModelObject(), getLocale());
                    target.appendJavaScript("""
                        const inputEl = document.getElementById("%s");
                        inputEl.value = "%s"\
                        """.formatted(markupId, value));
                }
            }
        }
    }

    public interface Event extends IClusterable {


        record ItemUpdated() implements Event {
        }
    }
}
