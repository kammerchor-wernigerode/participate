package de.vinado.wicket.participate.components.forms.validator;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationMessage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class ConditionalValidator<T> implements IValidator<T> {

    private IModel<String> errorMessage;

    public ConditionalValidator(final IModel<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public IModel<String> getErrorMessage() {
        return errorMessage;
    }

    public abstract boolean getCondition(final T value);

    @Override
    public void validate(final IValidatable<T> validatable) {
        if (getCondition(validatable.getValue())) {
            validatable.error((IValidationError) messageSource ->
                    new NotificationMessage(ConditionalValidator.this.getErrorMessage()));
        }
    }
}
