package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now1,
                                                          LocalDateTime now2, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    // брал ли пользователь эту вещь в аренду и завершилась ли аренда
    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime now
    );

}