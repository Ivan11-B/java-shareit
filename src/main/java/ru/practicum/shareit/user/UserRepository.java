package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    User save(User user);

    User update(User user);

    Optional<User> getUserById(Long id);

    void delete(Long id);
}
