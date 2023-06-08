package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId);

    @Query("select i from Item as i " +
            "where (LCASE(i.name) LIKE LCASE(concat('%',?1, '%')) " +
            "or LCASE(i.description) like LCASE(concat('%',?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameOrDescription(String text);
}
