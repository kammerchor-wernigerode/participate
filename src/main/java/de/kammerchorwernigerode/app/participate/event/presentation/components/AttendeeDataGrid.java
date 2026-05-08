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
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalHiddenEventBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.LocalDateTimeFormControl;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

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


            WebMarkupContainer periodCell = new WebMarkupContainer("periodCell");
            AttendeeCells.decorateStatus(periodCell, invitationStatusModel);
            form.add(periodCell);

            Label periodLabel = new Label("periodLabel", model.combineWith(item.getModel(), this::printPeriod));
            periodLabel.add(new TooltipBehavior(model.map(this::printRange)));
            periodCell.add(periodLabel);

            Modal modal = new Modal("modal")
                .centered(true)
                .size(Modal.Size.SMALL)
                .title(new ResourceModel("attendee.presence"))
                .content(id -> new PeriodModalContent(id, model))
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
            dto.setFrom(LocalDateTime.ofInstant(entry.getFromInstant(), entry.getStartZoneId()));
            dto.setTo(LocalDateTime.ofInstant(entry.getToInstant(), entry.getEndZoneId()));
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

        private String printRange(AttendeeDto dto) {
            Session session = Session.get();
            Locale locale = session.getLocale();
            DateTimeFormatter formatter = FORMATTER.localizedBy(locale);

            return formatter.format(dto.getFrom()) + "–" + formatter.format(dto.getTo());
        }

        private String printPeriod(AttendeeDto dto, AttendeeDetailsEntry entry) {
            AttendanceLabel label = create(dto, entry);

            if (!label.hasCustomFrom() && !label.hasCustomTo()) {
                return getString("attendance.full");
            }

            Map<String, Object> vars = new HashMap<>();
            vars.put("from", getString(label.from().resourceKey()));
            vars.put("to", getString(label.to().resourceKey()));

            String key = switch ((label.hasCustomFrom() ? 1 : 0) + (label.hasCustomTo() ? 2 : 0)) {
                case 1 -> "attendance.from";
                case 2 -> "attendance.to";
                case 3 -> "attendance.fromTo";
                default -> "attendance.full";
            };

            return getString(key, () -> vars);
        }

        private AttendanceLabel create(AttendeeDto dto, AttendeeDetailsEntry entry) {
            ZonedDateTime eventStart = entry.getStartInstant().atZone(entry.getStartZoneId());
            ZonedDateTime eventEnd = entry.getEndInstant().atZone(entry.getEndZoneId());
            ZonedDateTime from = dto.getFrom().atZone(entry.getStartZoneId());
            ZonedDateTime to = dto.getTo().atZone(entry.getEndZoneId());

            boolean hasCustomFrom = !from.toInstant().equals(eventStart.toInstant());
            boolean hasCustomTo = !to.toInstant().equals(eventEnd.toInstant());

            DayPeriodLabel fromLabel = new DayPeriodLabel(from.getDayOfWeek(), toDayPeriod(from));
            DayPeriodLabel toLabel = new DayPeriodLabel(to.getDayOfWeek(), toDayPeriod(to));
            return new AttendanceLabel(hasCustomFrom, hasCustomTo, fromLabel, toLabel);
        }

        private DayPeriod toDayPeriod(ZonedDateTime dateTime) {
            return switch (dateTime.getHour()) {
                case 0, 1, 2, 3, 4, 5 -> DayPeriod.NIGHT;
                case 6, 7, 8, 9, 10, 11 -> DayPeriod.MORNING;
                case 12, 13, 14, 15, 16 -> DayPeriod.AFTERNOON;
                case 17, 18, 19, 20, 21, 22, 23 -> DayPeriod.EVENING;
                default -> throw new IllegalStateException();
            };
        }


        private static class PeriodModalContent extends GenericPanel<AttendeeDto> {

            public PeriodModalContent(String id, IModel<AttendeeDto> model) {
                super(id, model);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();

                IModel<AttendeeDto> model = getModel();

                IModel<LocalDateTime> fromModel = LambdaModel.of(model, AttendeeDto::getFrom, AttendeeDto::setFrom);
                LocalDateTimeFormControl fromControl = new LocalDateTimeFormControl("from", fromModel);
                fromControl.setLabel(new ResourceModel("AttendeeForm.from"));
                add(fromControl);

                IModel<LocalDateTime> toModel = LambdaModel.of(model, AttendeeDto::getTo, AttendeeDto::setTo);
                LocalDateTimeFormControl toControl = new LocalDateTimeFormControl("to", toModel);
                toControl.setLabel(new ResourceModel("AttendeeForm.to"));
                add(toControl);
            }
        }
    }

    private record AttendanceLabel(boolean hasCustomFrom, boolean hasCustomTo, DayPeriodLabel from, DayPeriodLabel to) {
    }

    private record DayPeriodLabel(DayOfWeek dayOfWeek, DayPeriod period) {

        public String resourceKey() {
            return String.format("dayPeriod.%s.%s", dayOfWeek, period);
        }
    }

    private enum DayPeriod {

        NIGHT,
        MORNING,
        AFTERNOON,
        EVENING,
        ;
    }
}
