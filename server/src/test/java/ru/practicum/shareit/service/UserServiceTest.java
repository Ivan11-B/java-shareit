package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    Long id = 1L;
    String name = "Tom";
    String email = "Tom@mail.ru";

    @Test
    void getAll_shouldReturnListUsers() {
        List<User> users = List.of(User.builder().id(id).name(name).email(email).build());
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(id).name(name).email(email).build()));

        List<User> actualUsers = userService.getAll();

        assertNotNull(actualUsers);
        assertThat(actualUsers).containsExactlyElementsOf(users);
    }

    @Test
    void create_shouldReturnUser() {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.save(User.builder().id(id).name(name).email(email).build()))
                .thenReturn(User.builder().id(id).name(name).email(email).build());

        User savedUser = userService.create(user);

        assertNotNull(savedUser.getId());
        assertThat(savedUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
    }

    @Test
    void update_shouldReturnUpdateUser() {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(User.builder().id(id).name(name).email(email).build()))
                .thenReturn(User.builder().id(id).name(name).email(email).build());
        User updatedUser = userService.update(user);

        assertNotNull(updatedUser.getId());
        assertThat(updatedUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.findById(id))
                .thenReturn(Optional.ofNullable(User.builder().id(id).name(name).email(email).build()));

        User actualUser = userService.getUserById(id);

        assertNotNull(actualUser);
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void delete_doNothing() {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);
    }

    @Test
    void create_createDoubleEmail_shouldReturnThrow() {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.save(user))
                .thenThrow(new DuplicateException("Ошибка"));

        assertThrows(DuplicateException.class, () -> userService.create(user));
    }

    @Test
    void delete_deleteNotFoundUser_shouldReturnThrow() {
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(id));
    }
}