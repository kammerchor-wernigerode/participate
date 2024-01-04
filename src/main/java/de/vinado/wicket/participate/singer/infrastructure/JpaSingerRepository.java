package de.vinado.wicket.participate.singer.infrastructure;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.singer.model.SingerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class JpaSingerRepository implements SingerRepository {

    @NonNull
    private final SingerRecordRepository recordRepository;
    @NonNull
    private final PersonService personService;

    @Override
    public Optional<Singer> findBy(long id) {
        return recordRepository.findById(id);
    }

    @Override
    public Stream<Singer> listInactiveSingers() {
        return personService.listAllSingers()
            .filter(not(Singer::isActive));
    }
}
