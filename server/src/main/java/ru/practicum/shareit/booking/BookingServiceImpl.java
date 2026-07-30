package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    private User validationUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id {} не нейден", userId);
                    throw new NotFoundException(format("Пользователь с id %d не нейден", userId));
                });
    }

    private Item validationItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь с id {} не найдена", itemId);
                    throw new NotFoundException(format("Вещь с id %d не найдена", itemId));
                });
    }

    private Booking validationBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Бронирование с id {} не найдено", bookingId);
                    throw new NotFoundException(format("Бронирование с id %d не найдено", bookingId));
                });
    }

    private void validationDate(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start) || start.isBefore(LocalDateTime.now())) {
            log.warn("Некорректные даты бронирования");
            throw new ValidationException("Некорректные даты бронирования");
        }
    }

    @Override
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        log.info("Создание бронирования {} от пользователя {}", bookingCreateDto, userId);
        User user = validationUser(userId);
        Item item = validationItem(bookingCreateDto.getItemId());
        if (item.getOwner().getId().equals(user.getId())) {
            log.warn("Нельзя бронировать свою вещь");
            throw new ValidationException("Нельзя бронировать свою вещь");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            log.warn("Вещь недоступна");
            throw new ValidationException("Вещь недоступна");
        }
        validationDate(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Booking booking = BookingMapper.toBooking(bookingCreateDto, item, user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования с id {} от пользователя с id {} со статусом {}",
                bookingId, userId, approved);
        Booking booking = validationBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Подтвердить бронирование может только владелец");
            throw new ValidationException("Подтвердить бронирование может только владелец");
        }
        validationUser(userId);
        if (booking.getStatus() != BookingStatus.WAITING) {
            log.warn("Статус бронирования уже изменен");
            throw new ValidationException("Статус бронирования уже изменен");
        }
        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        log.info("Получение информации о бронировании с id {} от пользователя с id {}",
                bookingId, userId);
        User user = validationUser(userId);
        Booking booking = validationBooking(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(user.getId());
        boolean isOwner = booking.getItem().getOwner().getId().equals(user.getId());
        if (!isBooker && !isOwner) {
            log.warn("Нет доступа к бронированию");
            throw new NotFoundException("Нет доступа к бронированию");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getUserBookings(Long userId, BookingState state) {
        log.info("Получение информации о бронированиях пользователя с id {} со статусом {}",
                userId, state);
        validationUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, sort);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now, sort);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        log.info("Получение информации о бронированиях у пользователя c id {} со статусом {}",
                userId, state);
        validationUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings =  switch (state) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default -> bookingRepository.findByItemOwnerId(userId, sort);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}