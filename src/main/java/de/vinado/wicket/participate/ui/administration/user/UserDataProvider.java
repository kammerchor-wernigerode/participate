package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.repeater.table.FunctionalDataProvider;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class UserDataProvider extends FunctionalDataProvider<User> {

    private static final long serialVersionUID = -7222948700443699490L;

    private final UserService userService;

    @Override
    protected Stream<User> load() {
        return userService.getAll().stream();
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
