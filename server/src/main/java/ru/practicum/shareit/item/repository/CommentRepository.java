package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    @Query("SELECT c " +
            "FROM Comment AS c " +
            "JOIN c.item AS i " +
            "WHERE i.id = :itemId " +
            "AND LOWER(c.text) LIKE LOWER(concat('%', :text ,'%')) " +
            "ORDER BY c.created DESC")
    List<Comment> searchByText(@Param("itemId") Long itemId, @Param("text") String text, Pageable page);

    List<Comment> findAllByItemIdOrderByCreatedDesc(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> ids);
}