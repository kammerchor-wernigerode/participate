package de.vinado.wicket.participate.singer.infrastructure;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.singer.model.SingerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class JpaSingerRepository implements SingerRepository {

    @NonNull
    private final SingerRecordRepository recordRepository;

    @Override
    public Optional<Singer> findBy(long id) {
        return recordRepository.findById(id);
    }
}
