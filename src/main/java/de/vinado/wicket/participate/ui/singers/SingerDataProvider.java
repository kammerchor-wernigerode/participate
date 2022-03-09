package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@RequiredArgsConstructor
public class SingerDataProvider extends SortableDataProvider<Singer, SerializableFunction<Singer, ?>> {

    private final IModel<SingerFilter> filterModel;
    private final PersonService personService;

    @Override
    public Iterator<? extends Singer> iterator(long first, long count) {
        return streamFiltered()
            .sorted(Comparator.comparing(keyExtractor(), comparator()))
            .skip(first).limit(count)
            .iterator();
    }

    private Function<Singer, String> keyExtractor() {
        return getSort().getProperty().andThen(SingerDataProvider::toString);
    }

    private static String toString(Object property) {
        return null == property ? null : property.toString();
    }

    private Comparator<String> comparator() {
        Comparator<String> comparator = getSort().isAscending()
            ? Comparator.naturalOrder()
            : Comparator.reverseOrder();
        return Comparator.nullsFirst(comparator);
    }

    @Override
    public long size() {
        return streamFiltered().count();
    }

    private Stream<Singer> streamFiltered() {
        return streamPreFiltered()
            .filter(filterModel.getObject());
    }

    private Stream<Singer> streamPreFiltered() {
        return filterModel.getObject().isShowAll()
            ? personService.listAllSingers()
            : personService.getSingers().stream();
    }

    @Override
    public IModel<Singer> model(Singer singer) {
        return new SingerModel(singer);
    }


    private static final class SingerModel extends Model<Singer> {

        private static final long serialVersionUID = 4702909842661604922L;

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
