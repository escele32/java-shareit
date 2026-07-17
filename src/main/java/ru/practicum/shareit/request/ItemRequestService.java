package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getOwnerRequests(Long userId);

    Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
