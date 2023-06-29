package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long>, QuerydslPredicateExecutor<RequestItem> {
    @Query("SELECT req " +
            "FROM RequestItem AS req " +
            "JOIN FETCH req.requestor AS u " +
            "WHERE u.id = :id " +
            "ORDER BY req.created DESC")
    List<RequestItem> findAllByRequestorId(@Param("id") Long requestorId);

    @Query("SELECT req " +
            "FROM RequestItem AS req " +
            "WHERE req.requestor.id != :userId")
    @EntityGraph(attributePaths = "items")
    Page<RequestItem> findAllPaged(Pageable page, @Param("userId") Long userId);
}