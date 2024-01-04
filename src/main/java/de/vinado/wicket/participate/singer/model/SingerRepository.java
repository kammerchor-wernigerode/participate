package de.vinado.wicket.participate.singer.model;

import de.vinado.wicket.participate.model.Singer;

import java.util.Optional;
import java.util.stream.Stream;

public interface SingerRepository {

    Optional<Singer> findBy(long id);

    Stream<Singer> listInactiveSingers();
}
