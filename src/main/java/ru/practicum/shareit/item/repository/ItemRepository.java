package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId, Pageable page);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LCASE(i.name) LIKE LCASE(concat('%',?1, '%')) " +
            "OR LCASE(i.description) LIKE LCASE(concat('%',?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameOrDescription(String text, Pageable page);
}