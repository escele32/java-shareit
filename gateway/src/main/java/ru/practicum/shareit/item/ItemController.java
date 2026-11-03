package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.ApiPath;

import java.util.List;

@RestController
@RequestMapping(ApiPath.ITEMS)
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ApiPath.HEADER) Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи от пользователя c id {} с телом {}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping(ApiPath.ITEMID)
    public ResponseEntity<Object> update(@RequestHeader(ApiPath.HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи c id {} от пользователя id {} с телом {}", itemId, userId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping(ApiPath.ITEMID)
    public ResponseEntity<Object> getById(@RequestHeader(ApiPath.HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Запрос на отображение вещи c id {} от пользователя id {}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(ApiPath.HEADER) Long userId) {
        log.info("Запрос на отображение вещей пользователя c id = {}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping(ApiPath.SEARCH)
    public ResponseEntity<Object> search(@RequestHeader(ApiPath.HEADER) Long userId,
                                         @RequestParam String text) {
        log.info("Запрос на поиск вещей от пользователя c id {} с текстом: {}", userId, text);
        if (text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return itemClient.search(userId, text);
    }

    @PostMapping(ApiPath.ITEMIDCOMMENT)
    public ResponseEntity<Object> addComment(@RequestHeader(ApiPath.HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @DeleteMapping(ApiPath.ITEMID)
    public void delete(@RequestHeader(ApiPath.HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id {} от пользователя с id {}", itemId, userId);
        itemClient.delete(userId, itemId);
    }
}
