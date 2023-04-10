package de.vinado.wicket.participate.singer.model;

import de.vinado.wicket.participate.model.Singer;

import java.util.Optional;

public interface SingerRepository {

    Optional<Singer> findBy(long id);
}
