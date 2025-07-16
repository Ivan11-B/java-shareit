package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapper commentMapper;

    Long userId = 1L;
    String nameUser = "Tom";
    String email = "Tom@mail.ru";

    @Test
    void toDto() {
        User user = User.builder().id(userId).name(nameUser).email(email).build();
        List<Comment> comments = List.of(Comment.builder().text("text").author(user).build());
        List<CommentDto> result = commentMapper.toDto(comments);
        assertNotNull(result);
    }
}
