package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.ApiPath;

import java.util.Collection;

@RestController
@RequestMapping(ApiPath.REQUESTS)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(ApiPath.HEADER) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на создание запроса вещи {} от пользователя c id {}", itemRequestDto, userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getOwnerRequests(@RequestHeader(ApiPath.HEADER) Long userId) {
        log.info("Запрос на получение своих запросов пользователя с id {}", userId);
        return itemRequestService.getOwnerRequests(userId);
    }

    @GetMapping(ApiPath.ALL)
    public Collection<ItemRequestDto> getAllRequests(@RequestHeader(ApiPath.HEADER) Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех запросов пользователя с id {} с {} в количестве {}", userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping(ApiPath.REQUESTID)
    public ItemRequestDto getById(@RequestHeader(ApiPath.HEADER) Long userId,
                                  @PathVariable Long requestId) {
        log.info("Запрос на получение запроса с id {} пользователя с id {}", requestId, userId);
        return itemRequestService.getById(userId, requestId);
    }
}
