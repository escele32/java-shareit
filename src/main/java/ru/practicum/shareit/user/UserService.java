package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

    Optional<UserDto> getUserId(Long userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);
}
