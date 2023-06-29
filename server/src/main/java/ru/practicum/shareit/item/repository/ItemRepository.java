package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameOrDescription(@Param("text") String text, Pageable pageable);

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);
}