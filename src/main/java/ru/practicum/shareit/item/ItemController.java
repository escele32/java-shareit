package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка вещей владельца с id {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи {} пользователем с id {}", itemDto, userId);
        return itemService.saveItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с id {} от пользователя с id {}", itemId, userId);
        return itemService.getItemId(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("Запрос на поиск вещей от пользователя с id {} с текстом {}", userId, text);
        return itemService.search(userId, text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи {} с id {} от пользователя с id {}", itemDto, itemId, userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id {} от пользователя с id {}", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}
