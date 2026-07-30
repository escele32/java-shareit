package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ApiPath;


@Controller
@RequestMapping(ApiPath.BOOKINGS)
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(ApiPath.HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from",
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size",
                                                      defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId {}, from {}, size {}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(ApiPath.HEADER) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId {}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping(ApiPath.BOOKINGID)
    public ResponseEntity<Object> getBooking(@RequestHeader(ApiPath.HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping(ApiPath.BOOKINGID)
    public ResponseEntity<Object> approve(@RequestHeader(ApiPath.HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение бронирования id {} от пользователя с id {} статус {}", bookingId, userId,
                approved);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping(ApiPath.OWNER)
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(ApiPath.HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Запрос на получение инф-ии о бронированиях у пользователя id {} со статусом {}", userId, stateParam);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getOwnerBookings(userId, state);
    }
}