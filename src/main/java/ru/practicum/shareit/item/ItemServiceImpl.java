package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    private Item validationItemId(Long itemId) {
        if (itemId == null) {
            log.info("Id вещи не может быть null");
            throw new ValidationException("Id вещи не может быть null");
        }
        return itemRepository.findItemById(itemId).orElseThrow(() -> {
            log.info("Вещ с id {} не найдена", itemId);
            throw new NotFoundException(format("Вещ с id %d не найдена\n", itemId));
        });
    }

    private User validationUserId(Long userId) {
        if (userId == null) {
            log.warn("Id пользователя не может быть null");
            throw new ValidationException("Id пользователя не может быть null");
        }
        return userRepository.findUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException(format("Пользователь с id %d не найден\n", userId));
        });
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        log.info("Получение списка вещей владельца");
        User user = validationUserId(userId);
        return itemRepository.findItems()
                .stream()
                .filter(item -> item.getOwner() != null)
                .filter(item -> item.getOwner().getId().equals(user.getId()))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        log.info("Создание вещи {} пользователем {}", itemDto, userId);
        User user = validationUserId(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        Item saveItem = itemRepository.save(item);
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("Удаление вещи с id {}", itemId);
        Item item = validationItemId(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} пытается удалить чужую вещь {}", userId, itemId);
            throw new ValidationException("Нельзя удалять чужую вещь");
        }
        itemRepository.delete(item.getId());
    }

    @Override
    public Optional<ItemDto> getItemId(Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        Item item = validationItemId(itemId);
        return itemRepository.findItemById(item.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .findFirst();
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи {} c id = {} от пользователя {}", itemDto, itemId, userId);
        User user = validationUserId(userId);
        Item item = validationItemId(itemId);
        if (!item.getOwner().getId().equals(user.getId())) {
            log.warn("Пользователь с id {} не является владельцем вещи с id {}", user.getId(), item.getId());
            throw new NotFoundException(format("Пользователь с id %d не является владельцем вещи с id %d",
                    user.getId(), item.getId()));
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item saveItem = itemRepository.update(item);
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        log.info("Поиск вещей пользователя c id = {} с текстом {}", userId, text);
        if (text == null || text.isBlank()) {
            log.warn("Строка поиска пуста");
            return List.of();
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
