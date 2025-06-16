package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User create(User user);

    User update(User user);

    User getUserById(Long id);

    void delete(Long id);
}