package de.vinado.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationMessage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.danekja.java.util.function.serializable.SerializablePredicate;

/**
 * @author Vincent Nadoll
 */
public class ConditionalValidator<T> implements IValidator<T> {

    private static final long serialVersionUID = 3523756055291241981L;

    private final SerializablePredicate<T> condition;
    private final IModel<String> errorMessage;

    public ConditionalValidator(SerializablePredicate<T> condition, IModel<String> errorMessage) {
        this.condition = condition;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        if (condition.negate().test(validatable.getValue())) {
            validatable.error(messageSource -> new NotificationMessage(errorMessage));
        }
    }
}
