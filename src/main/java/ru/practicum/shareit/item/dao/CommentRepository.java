package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> ids);

    @Query("select c from Comment as c " +
            "JOIN Item as i " +
            "where i.id=?1 AND LCASE(c.text) LIKE LCASE(concat('%',?2,'%')) " +
            "order by c.created DESC")
    List<Comment> searchByText(Long itemId, String text);
}
