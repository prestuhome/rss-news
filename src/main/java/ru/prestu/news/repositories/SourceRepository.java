package ru.prestu.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prestu.news.domain.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {

}
