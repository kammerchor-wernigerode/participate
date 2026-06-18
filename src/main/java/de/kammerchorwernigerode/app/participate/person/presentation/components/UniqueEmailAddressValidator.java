package de.kammerchorwernigerode.app.participate.person.presentation.components;

import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecordRepository;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailAddressValidator implements IValidator<String> {

    private final PersonRecordRepository personRecordRepository;

    @Override
    public void validate(IValidatable<String> validatable) {
        String value = validatable.getValue();
        if (Strings.isEmpty(value)) {
            return;
        }

        boolean exist = personRecordRepository.existsByEmailAddressIgnoreCase(value);
        if (!exist) {
            return;
        }

        ValidationError error = new ValidationError(this);
        validatable.error(error);
    }
}
