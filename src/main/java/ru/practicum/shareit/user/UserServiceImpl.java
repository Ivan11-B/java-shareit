package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateException ex) {
            throw ex;
        }
    }

    @Override
    public User update(User user) {
        User currentUser = getUserById(user.getId());
        User updateUser = new User(currentUser.getId(), currentUser.getName(), currentUser.getEmail());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return userRepository.update(updateUser);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User ID= " + id + " не найден!"));
    }

    @Override
    public void delete(Long id) {
        getUserById(id);
        userRepository.delete(id);
    }
}