package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class UserDataProvider extends SortableDataProvider<User, SerializableFunction<User, ?>> {

    private static final long serialVersionUID = -7222948700443699490L;

    private final UserService userService;

    @Override
    public Iterator<? extends User> iterator(long first, long count) {
        return userService.getAll().stream()
            .sorted(Comparator.comparing(keyExtractor(), keyComparator()))
            .skip(first).limit(count)
            .iterator();
    }

    private Function<User, String> keyExtractor() {
        return getSort().getProperty().andThen(UserDataProvider::toString);
    }

    private static String toString(Object property) {
        return null == property ? null : property.toString();
    }

    private Comparator<String> keyComparator() {
        Comparator<String> comparator = getSort().isAscending()
            ? Comparator.naturalOrder()
            : Comparator.reverseOrder();
        return Comparator.nullsFirst(comparator);
    }

    @Override
    public long size() {
        return userService.getAll().size();
    }

    @Override
    public IModel<User> model(User user) {
        return new UserModel(user);
    }


    private static final class UserModel extends Model<User> {

        private static final long serialVersionUID = 1268287923639115228L;

        public UserModel(User user) {
            super(user);
        }

        @Override
        public int hashCode() {
            User user = getObject();
            return Objects.hash(
                user.getId(),
                user.getUsername(),
                user.getPasswordSha256(),
                user.isAdmin(),
                user.isEnabled(),
                user.isActive(),
                user.getPerson()
            );
        }
    }
}
