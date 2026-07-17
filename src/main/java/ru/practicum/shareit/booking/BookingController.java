package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.util.ApiPath;

import java.util.Collection;

@RestController
@RequestMapping(ApiPath.BOOKINGS)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(ApiPath.HEADER) Long userId,
                             @Valid @RequestBody BookingCreateDto dto) {
        log.info("Запрос на бронирование вещи от пользователя с id {} с телом {}", userId, dto);
        return bookingService.create(userId, dto);
    }

    @PatchMapping(ApiPath.BOOKINGID)
    public BookingDto approve(@RequestHeader(ApiPath.HEADER) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info("Запрос на бронирование с id {} от пользователя с id {} статус {}", bookingId, userId, approved);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping(ApiPath.BOOKINGID)
    public BookingDto getById(@RequestHeader(ApiPath.HEADER) Long userId,
                              @PathVariable Long bookingId) {
        log.info("Запрос на получение инф-ии о бронирование с id {} от пользователя с id {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(
            @RequestHeader(ApiPath.HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрос на получение инф-ии о бронированиях пользователя с id {} со статусом {}",  userId, state);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping(ApiPath.OWNER)
    public Collection<BookingDto> getOwnerBookings(
            @RequestHeader(ApiPath.HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Запрос на получение инф-ии о бронированиях у владельца с id  {} со статусом {}",  userId, state);
        return bookingService.getOwnerBookings(userId, state);
    }
}
