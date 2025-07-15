package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException("Email уже существует");
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        User currentUser = getUserById(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            currentUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equals(currentUser.getEmail())) {
                if (validateEmail(user.getEmail())) {
                    throw new DuplicateException("Email уже существует");
                }
                currentUser.setEmail(user.getEmail());
            }
        }
        return userRepository.save(currentUser);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User ID= " + id + " не найден!"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User ID= " + id + " не найден!");
        }
        userRepository.deleteById(id);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}