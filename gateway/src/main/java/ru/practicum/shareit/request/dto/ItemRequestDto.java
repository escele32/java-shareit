package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    LocalDateTime created;
    List<RequestDto> items;
}
