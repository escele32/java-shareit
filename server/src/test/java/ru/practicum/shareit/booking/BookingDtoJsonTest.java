package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingDtoJsonTest {
    JacksonTester<BookingDto> dtoJacksonTester;

    @Test
    void setDtoJacksonTester() throws Exception {
        UserDto booker = UserDto.builder()
                .id(3L)
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2026, 1, 16, 21, 30, 20))
                .end(LocalDateTime.of(2026, 2, 16, 21, 30, 20))
                .build();
        BookingItemDto bookingItemDto = BookingItemDto.builder()
                .id(bookingCreateDto.getItemId())
                .name("Knife")
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .id(2L)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(bookingItemDto)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        JsonContent<BookingDto> dtoJsonContent = dtoJacksonTester.write(bookingDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-01-16T21:30:20");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-02-16T21:30:20");
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingItemDto.getName());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(3);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(booker.getName());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(booker.getEmail());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
