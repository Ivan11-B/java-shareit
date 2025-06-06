package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public User toEntityUpdate(UserDtoUpdate userDtoUpdate) {
        return User.builder()
                .name(userDtoUpdate.getName())
                .email(userDtoUpdate.getEmail())
                .build();
    }
}