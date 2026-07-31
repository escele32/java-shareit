package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ApiPath;

@RestController
@RequestMapping(ApiPath.USERS)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping(ApiPath.USERID)
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя {} с id {}", userDto, userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping(ApiPath.USERID)
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с id {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAll();
    }

    @DeleteMapping(ApiPath.USERID)
    public void delete(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id {}", userId);
        userClient.delete(userId);
    }
}
