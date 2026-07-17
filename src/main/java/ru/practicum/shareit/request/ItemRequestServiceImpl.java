package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    private User validationUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id {} не найден", userId);
                    throw new NotFoundException(format("Пользователь с id %d не найден", userId));
                });
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Создание запроса вещи {} пользователем {}", itemRequestDto, userId);
        User user = validationUserById(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toDto(itemRequestRepository.save(request));
    }

    @Override
    public Collection<ItemRequestDto> getOwnerRequests(Long userId) {
        log.info("Получение своих запросов от пользователя с id {}", userId);
        User user = validationUserById(userId);
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())
                .stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Получение всех запросов пользователя {} с {} в количестве {}", userId, from, size);
        if (size == null || size <= 0) {
            log.warn("Параметр size должен быть больше 0");
            throw new ValidationException("Параметр size должен быть больше 0");
        }
        int page = from / size;
        validationUserById(userId);
        return itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        log.info("Получение запроса с id {} от пользователя с id {}", requestId, userId);
        validationUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id {} не найден", requestId);
                    throw new NotFoundException(format("Запрос с id {} не найден", requestId));
                });
        return ItemRequestMapper.toDto(itemRequest);
    }
}
