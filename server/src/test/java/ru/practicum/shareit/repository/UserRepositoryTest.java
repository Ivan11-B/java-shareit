package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    Long id = 1L;
    String name = "Tom";
    String email = "Tom@mail.ru";

    @Test
    void shouldSaveAndFindUserById() {
        User user = User.builder().name(name).email(email).build();

        User savedUser = userRepository.save(user);
        Optional<User> actualUser = userRepository.findById(savedUser.getId());

        assertNotNull(savedUser.getId());
        assertThat(actualUser.get()).usingRecursiveComparison().isEqualTo(savedUser);
    }


    @Test
    void shouldNotAllowDuplicateEmails() {
        User user = User.builder().name(name).email(email).build();
        userRepository.saveAndFlush(user);

        User user2 = User.builder().name(name).email(email).build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
