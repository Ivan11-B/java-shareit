package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository

public class UserRepositoryImpl implements UserRepository {
    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        checkEmail(user.getEmail());
        Long idUser = nextId();
        user.setId(idUser);
        users.put(idUser, user);
        return user;
    }

    @Override
    public User update(User user) {
        User userToMap = users.get(user.getId());
        if (user.getEmail().equals(userToMap.getEmail())) {
            users.put(user.getId(), user);
        } else {
            checkEmail(user.getEmail());
            emails.remove(userToMap.getEmail());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        User currentUser = users.get(id);
        if (currentUser == null) {
            return Optional.empty();
        } else {
            return Optional.of(currentUser);
        }
    }

    @Override
    public void delete(Long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    private Long nextId() {
        return id++;
    }

    private boolean checkEmail(String email) {
        if (emails.contains(email)) {
            throw new DuplicateException(email + " уже существует!");
        } else {
            emails.add(email);
            return true;
        }
    }
}