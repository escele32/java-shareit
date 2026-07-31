package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.ApiPath;

@RestController
@RequestMapping(ApiPath.REQUESTS)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ApiPath.HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на создание запроса вещи {} от пользователя {}", itemRequestDto, userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader(ApiPath.HEADER) Long userId) {
        log.info("Запрос на получение своих запросов пользователя {}", userId);
        return itemRequestClient.getOwnerRequests(userId);
    }

    @GetMapping(ApiPath.ALL)
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(ApiPath.HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех запросов пользователя {} с {} в количестве {}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping(ApiPath.REQUESTID)
    public ResponseEntity<Object> getById(@RequestHeader(ApiPath.HEADER) Long userId,
                                          @PathVariable Long requestId) {
        log.info("Запрос на получение запроса с id {} пользователя с id {}", requestId, userId);
        return itemRequestClient.getById(userId, requestId);
    }
}
