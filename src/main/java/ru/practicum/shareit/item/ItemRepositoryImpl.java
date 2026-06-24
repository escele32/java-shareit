package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public Item save(Item item) {
        item.setId(atomicLong.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> findItems() {
        return items.values();
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> search(String query) {
        String text = query.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toUpperCase().contains(text))
                .toList();
    }

    @Override
    public void clean() {
        items.clear();
    }
}
