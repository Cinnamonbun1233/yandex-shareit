package ru.practicum.shareit.user.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    private Long id;
    private String name;
    @EqualsAndHashCode.Include
    private String email;
}