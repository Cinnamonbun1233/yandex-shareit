package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

//DONE!!!
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LCASE(i.name) LIKE LCASE(concat('%',?1, '%')) " +
            "OR LCASE(i.description) LIKE LCASE(concat('%',?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameOrDescription(String text);
}