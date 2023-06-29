package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.user.model.User;

//Готово
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

}