package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    Long id;
    String name;
    Long ownerId;
}
