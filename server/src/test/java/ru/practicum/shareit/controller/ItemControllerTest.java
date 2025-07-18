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
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class, ItemMapper.class, CommentMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @MockBean
    UserMapper userMapper;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    Long id = 1L;
    String name = "Phone";
    String description = "Call";
    boolean available = true;

    Long userId = 1L;
    String nameUser = "Tom";
    String emailUser = "Tom@mail.ru";

    @Test
    void createItem_shouldReturnItem() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemDto itemDto = ItemDto.builder().name(name).description(description).available(available).build();
        Item createdItem = Item.builder().id(id).name(name).description(description)
                .available(available).owner(user).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemService.createItem(any(Item.class), eq(userId)))
                .thenReturn(createdItem);

        MvcResult mvcResult = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemDto actualItemDto = mapper.readValue(responseBody, ItemDto.class);

        assertNotNull(actualItemDto.getId());
        assertNotNull(actualItemDto.getOwner());
        assertThat(actualItemDto).usingRecursiveComparison().ignoringFields("id", "owner").isEqualTo(itemDto);
    }

    @Test
    void updateItem_ahouldReturnUpdateItem() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(name).description(description).available(available).build();
        Item updateItem = Item.builder().id(id).name(name).description(description).available(available).owner(user).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemService.updateItem(any(Item.class), eq(userId)))
                .thenReturn(updateItem);

        MvcResult mvcResult = mvc.perform(patch("/items/{id}", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemDto actualItem = mapper.readValue(responseBody, ItemDto.class);

        assertNotNull(actualItem.getId());
        assertThat(actualItem).usingRecursiveComparison().ignoringFields("owner").isEqualTo(itemDto);
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemWithBookingsDto item = ItemWithBookingsDto.builder().id(id).name(name).description(description)
                .available(available).owner(userDto).build();
        when(itemService.getItemByIdWithBooking(id))
                .thenReturn(item);

        MvcResult mvcResult = mvc.perform(get("/items/{id}", id)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemWithBookingsDto actualItem = mapper.readValue(responseBody, ItemWithBookingsDto.class);

        assertNotNull(actualItem);
        assertThat(actualItem).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void getAll_shouldReturnListItems() throws Exception {
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        List<ItemWithBookingsDto> items = List.of(ItemWithBookingsDto.builder().id(id).name(name).description(description)
                .available(available).owner(userDto).build());
        when(itemService.findAll(userId))
                .thenReturn(items);

        MvcResult mvcResult = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemWithBookingsDto> actualListItems = mapper.readValue(responseBody,
                new TypeReference<List<ItemWithBookingsDto>>() {
                });

        assertNotNull(actualListItems);
        assertEquals(1, actualListItems.size());
    }

    @Test
    void searchByString_shouldReturnListItems() throws Exception {
        String searchText = "text";
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        List<Item> items = List.of(Item.builder().id(id).name(name).description(description)
                .available(available).owner(user).build());
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemService.searchByString(searchText))
                .thenReturn(items);

        MvcResult mvcResult = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemDto> actualListItems = mapper.readValue(responseBody,
                new TypeReference<List<ItemDto>>() {
                });

        assertNotNull(actualListItems);
        assertEquals(1, actualListItems.size());
    }

    @Test
    void createComment_shouldReturnComment() throws Exception {
        Long commentId = 1L;
        String text = "Text";
        LocalDateTime created = LocalDateTime.now();
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        CommentDto commentDto = CommentDto.builder().text(text).build();
        Comment createdComment = Comment.builder().id(commentId).text(text).author(user).created(created).build();

        when(itemService.addComment(any(Comment.class), eq(userId), eq(id)))
                .thenReturn(createdComment);

        MvcResult mvcResult = mvc.perform(post("/items/{itemId}/comment", id)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        CommentDto actualCommentDto = mapper.readValue(responseBody, CommentDto.class);

        assertNotNull(actualCommentDto.getId());
        assertEquals(actualCommentDto.getText(), commentDto.getText());
    }

    @Test
    void updateItem_shouldReturnThrow() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemDto itemDto = ItemDto.builder().id(id).name(name).description(description).available(available).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemService.updateItem(any(Item.class), eq(userId)))
                .thenThrow(new ItemOwnershipException(""));

        mvc.perform(patch("/items/{id}", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    void createComment_shouldReturnThrow() throws Exception {
        Long commentId = 1L;
        String text = "Text";
        LocalDateTime created = LocalDateTime.now();
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        CommentDto commentDto = CommentDto.builder().text(text).build();

        when(itemService.addComment(any(Comment.class), eq(userId), eq(id)))
                .thenThrow(new IllegalStateException(""));

        mvc.perform(post("/items/{itemId}/comment", id)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }
}