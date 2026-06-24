package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Collection<Item> findItems();

    Optional<Item> findItemById(Long itemId);

    void delete(Long itemId);

    Item update(Item item);

    Collection<Item> search(String query);

    void clean();
}
