package ru.prestu.news.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.prestu.news.domain.Item;
import ru.prestu.news.domain.Source;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByTitleIgnoreCaseContaining(String title, Pageable pageable);
    Page<Item> findAll(Pageable pageable);
    Item findFirstBySourceOrderByPubDateDesc(Source source);

}
