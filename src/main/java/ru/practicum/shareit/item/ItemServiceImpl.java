package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    private Item validationItemId(Long itemId) {
        if (itemId == null) {
            log.info("Id вещи не может быть null");
            throw new ValidationException("Id вещи не может быть null");
        }
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.info("Вещ с id {} не найдена", itemId);
            throw new NotFoundException(format("Вещ с id %d не найдена\n", itemId));
        });
    }

    private User validationUserId(Long userId) {
        if (userId == null) {
            log.warn("Id пользователя не может быть null");
            throw new ValidationException("Id пользователя не может быть null");
        }
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException(format("Пользователь с id %d не найден\n", userId));
        });
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        log.info("Получение списка вещей владельца");
        User user = validationUserId(userId);
        return itemRepository.findByOwnerId(userId)
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
        itemRepository.deleteById(item.getId());
    }

    @Override
    public Optional<ItemDto> getItemId(Long userId,Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        Item item = validationItemId(itemId);
        ItemDto itemDto = itemRepository.findById(item.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .findFirst().get();
        itemDto.setComments(commentRepository.findByItemId(itemId)
                .stream()
                .map(ItemMapper::toCommentDto)
                .toList());
        return Optional.of(itemDto);
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
        Item saveItem = itemRepository.save(item);
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

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Добавление комментария к вещи с id {} от пользователя c id = {} с текстом {}",
                itemId, userId, commentDto);
        User user = validationUserId(userId);
        Item item = validationItemId(itemId);
        boolean isHasPastBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(),
                user.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now());
        if (!isHasPastBooking) {
            log.warn("Пользователь с id {} не брал вещь с id {} в аренду\n",
                    user.getId(), item.getId());
            throw new ValidationException(format("Пользователь с id %d не брал вещь с id %d в аренду\n",
                    user.getId(), item.getId()));
        }
        Comment comment = ItemMapper.toComment(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);
        return ItemMapper.toCommentDto(savedComment);
    }
}