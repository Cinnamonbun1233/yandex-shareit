package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> ids);

    @Query("SELECT c " +
            "FROM Comment AS c " +
            "JOIN c.item AS i " +
            "WHERE i.id=?1 " +
            "AND LCASE(c.text) LIKE LCASE(concat('%',?2,'%')) " +
            "ORDER BY c.created DESC")
    List<Comment> searchByText(Long itemId, String text, Pageable page);
}