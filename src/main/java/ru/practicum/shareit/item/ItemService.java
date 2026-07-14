package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getOwnerItems(Long userId);

    ItemDto saveItem(Long userId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    Optional<ItemDto> getItemId(Long userId, Long itemId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> search(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
