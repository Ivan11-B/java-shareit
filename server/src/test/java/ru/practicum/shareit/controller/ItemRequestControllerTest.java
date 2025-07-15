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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class, ItemRequestMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    UserMapper userMapper;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    Long id = 1L;
    String description = "description";

    LocalDateTime created = LocalDateTime.now();
    Long userId = 1L;
    String nameUser = "Tom";
    String emailUser = "Tom@mail.ru";

    @Test
    void addItemRequest_shouldReturnItemRequest() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description(description).build();
        ItemRequest itemRequest = ItemRequest.builder().id(id).description(description).created(created).user(user).build();
        ItemRequestDto itemRequestResult = ItemRequestDto.builder().description(description).created(created).requester(userDto).build();
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemRequestService.addItemRequest(any(ItemRequest.class), eq(userId)))
                .thenReturn(itemRequest);

        MvcResult mvcResult = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemRequestDto actualItemRequestDto = mapper.readValue(responseBody, ItemRequestDto.class);

        assertNotNull(actualItemRequestDto.getId());
        assertThat(actualItemRequestDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(itemRequestResult);
    }

    @Test
    void getAllRequests_shouldReturnListItemRequests() throws Exception {
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        List<ItemResponseDto> itemRequests = List.of(ItemResponseDto.builder().id(id)
                .description(description).created(created).requester(userDto).build());
        when(itemRequestService.getAllRequests(userId))
                .thenReturn(itemRequests);

        MvcResult mvcResult = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemResponseDto> actualItemRequests = mapper.readValue(responseBody, new TypeReference<List<ItemResponseDto>>() {
        });

        assertNotNull(actualItemRequests);
        assertEquals(1, actualItemRequests.size());
    }

    @Test
    void getAllRequestAnotherUser_shouldReturnListItemRequests() throws Exception {
        User user = User.builder().id(userId).name(nameUser).email(emailUser).build();
        List<ItemRequest> itemRequests = List.of(ItemRequest.builder().id(id)
                .description(description).created(created).user(user).build());
        when(itemRequestService.getAllRequestsAnotherUsers(userId))
                .thenReturn(itemRequests);

        MvcResult mvcResult = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        List<ItemRequestDto> actualItemRequests = mapper.readValue(responseBody, new TypeReference<List<ItemRequestDto>>() {
        });

        assertNotNull(actualItemRequests);
        assertEquals(1, actualItemRequests.size());
    }

    @Test
    void getItemRequestById_shouldReturnItemResponseDto() throws Exception {
        UserDto userDto = UserDto.builder().id(userId).name(nameUser).email(emailUser).build();
        ItemResponseDto itemResponseDto = ItemResponseDto.builder().id(id).description(description)
                .created(created).requester(userDto).items(List.of()).build();
        when(itemRequestService.getItemRequestById(userId, id))
                .thenReturn(itemResponseDto);

        MvcResult mvcResult = mvc.perform(get("/requests/{requestId}", id)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemResponseDto actualItemResponseDto = mapper.readValue(responseBody, ItemResponseDto.class);

        assertNotNull(actualItemResponseDto);
    }
}