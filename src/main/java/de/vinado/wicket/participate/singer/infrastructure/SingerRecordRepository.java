package de.vinado.wicket.participate.singer.infrastructure;

import de.vinado.wicket.participate.model.Singer;
import org.springframework.data.jpa.repository.JpaRepository;

interface SingerRecordRepository extends JpaRepository<Singer, Long> {
}
