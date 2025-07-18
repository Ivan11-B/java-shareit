package ru.practicum.shareit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, UserMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    @MockBean
    UserService userService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    Long id = 1L;
    String name = "Tom";
    String email = "Tom@mail.ru";

    @Test
    void createUser_shouldReturnUser() throws Exception {

        UserDto userDto = UserDto.builder().name(name).email(email).build();
        User createdUser = User.builder().id(id).name(name).email(email).build();
        when(userService.create(any(User.class)))
                .thenReturn(createdUser);

        MvcResult mvcResult = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = mapper.readValue(responseBody, UserDto.class);

        assertNotNull(actualUserDto.getId());
        assertThat(actualUserDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(userDto);
    }

    @Test
    void updateUser_shouldReturnUpdateUser() throws Exception {

        UserDto userDto = UserDto.builder().id(id).name(name).email(email).build();
        User updatedUser = User.builder().id(id).name(name).email(email).build();
        when(userService.update(any(User.class)))
                .thenReturn(updatedUser);

        MvcResult mvcResult = mvc.perform(patch("/users/{id}", id)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = mapper.readValue(responseBody, UserDto.class);

        assertNotNull(actualUserDto.getId());
        assertThat(actualUserDto).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = User.builder().id(id).name(name).email(email).build();
        when(userService.getUserById(id))
                .thenReturn(user);

        MvcResult mvcResult = mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        UserDto actualUserDto = mapper.readValue(responseBody, UserDto.class);

        assertNotNull(actualUserDto);
    }


    @Test
    void getAll_shouldReturnListUsers() throws Exception {
        User user = User.builder().id(id).name(name).email(email).build();
        List<User> users = List.of(user);
        when(userService.getAll())
                .thenReturn(users);

        MvcResult mvcResult = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<UserDto> actualUsers = mapper.readValue(responseBody, new TypeReference<List<UserDto>>() {
        });

        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
    }

    @Test
    void deleteUser_doNothing() throws Exception {
        doNothing().when(userService).delete(id);

        mvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserById_shouldReturnThrow() throws Exception {

        when(userService.getUserById(id))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_duplicateEmail_shouldReturnThrow() throws Exception {
        User user = User.builder().name(name).email(email).build();
        when(userService.create(user))
                .thenThrow(new DuplicateException(""));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}