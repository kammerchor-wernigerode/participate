package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.repeater.table.FilterableDataProvider;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingerDataProvider extends FilterableDataProvider<Singer> {

    private final IModel<SingerFilter> filterModel;
    private final PersonService personService;

    public SingerDataProvider(IModel<SingerFilter> filter, PersonService personService) {
        super(filter);
        this.filterModel = filter;
        this.personService = personService;
    }

    @Override
    protected Stream<Singer> load() {
        return filterModel.getObject().isShowAll()
            ? personService.listAllSingers()
            : personService.getSingers().stream();
    }

    @Override
    public IModel<Singer> model(Singer singer) {
        return new SingerModel(singer);
    }


    private static final class SingerModel extends Model<Singer> {

        public SingerModel(Singer singer) {
            super(singer);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Model<?>)) return false;

            Singer that;
            try {
                Model<?> thatModel = (Model<?>) obj;
                that = (Singer) thatModel.getObject();
            } catch (ClassCastException e) {
                return false;
            }

            Singer singer = getObject();
            return new EqualsBuilder()
                .append(singer.getId(), that.getId())
                .append(singer.getFirstName(), that.getFirstName())
                .append(singer.getLastName(), that.getLastName())
                .append(singer.getEmail(), that.getEmail())
                .append(singer.getCreationDate(), that.getCreationDate())
                .append(singer.getLastModified(), that.getLastModified())
                .append(singer.getVoice(), that.getVoice())
                .append(singer.isActive(), that.isActive())
                .isEquals();
        }

        @Override
        public int hashCode() {
            Singer singer = getObject();
            return Objects.hash(
                singer.getId(),
                singer.getFirstName(),
                singer.getLastName(),
                singer.getEmail(),
                singer.getCreationDate(),
                singer.getLastModified(),
                singer.getVoice(),
                singer.isActive()
            );
        }
    }
}
