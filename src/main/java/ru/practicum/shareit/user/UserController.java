package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ApiPath;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPath.USERS)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение списка пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto saveUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя");
        return userService.saveUser(userDto);
    }

    @PatchMapping(ApiPath.USERID)
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя");
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping(ApiPath.USERID)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя по id");
        userService.deleteUser(userId);
    }

    @GetMapping(ApiPath.USERID)
    public Optional<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя по id");
        return userService.getUserId(userId);
    }
}