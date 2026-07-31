package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;
    Long requestId;
}
