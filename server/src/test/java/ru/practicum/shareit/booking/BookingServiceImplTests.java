package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImplTests {
    BookingService bookingService;
    ItemService itemService;
    UserService userService;
    BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImplTests(BookingService bookingService,
                                   ItemService itemService,
                                   UserService userService,
                                   BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Test
    void testBookingCreate() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userService.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemService.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingService.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        assertEquals(saveItem.getId(), saveBookingDto.getItem().getId());
        assertEquals(saveUser.getId(), saveBookingDto.getBooker().getId());
        assertEquals(BookingStatus.WAITING, saveBookingDto.getStatus());
        System.out.println(bookingService.getUserBookings(saveUser.getId(), BookingState.WAITING));
        bookingRepository.deleteById(saveBookingDto.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem.getId());
        userService.deleteUser(saveUser.getId());
        userService.deleteUser(saveOwner.getId());
    }

    @Test
    void testBookingGetById() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userService.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemService.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingService.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        assertEquals(saveBookingDto.getId(),
                bookingService.getById(saveUser.getId(), saveBookingDto.getId()).getId());
        System.out.println(bookingService.getById(saveUser.getId(), saveBookingDto.getId()));
        bookingRepository.deleteById(saveBookingDto.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem.getId());
        userService.deleteUser(saveUser.getId());
        userService.deleteUser(saveOwner.getId());
    }

    @Test
    void testBookingApprove() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userService.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemService.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingService.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        BookingDto approveBookingDto = bookingService.approve(saveOwner.getId(),
                saveBookingDto.getId(), true);
        assertEquals(BookingStatus.APPROVED, approveBookingDto.getStatus());
        System.out.println(bookingService.getOwnerBookings(saveOwner.getId(), BookingState.CURRENT));
        bookingRepository.deleteById(saveBookingDto.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem.getId());
        userService.deleteUser(saveUser.getId());
        userService.deleteUser(saveOwner.getId());
    }

    @Test
    void testBookingGetUserBookings() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userService.saveUser(ownerDto);
        UserDto userDto1 = UserDto.builder()
                .name("Mariia1")
                .email("mariia1@yandex.ru")
                .build();
        UserDto saveUser1 = userService.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Mariia2")
                .email("mariia2@yandex.ru")
                .build();
        UserDto saveUser2 = userService.saveUser(userDto2);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveOwner.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveOwner.getId(), itemDto2);
        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(saveItem1.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto1 = bookingService.create(saveUser1.getId(), bookingCreateDto1);
        assertNotNull(saveBookingDto1.getId());
        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(saveItem2.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto2 = bookingService.create(saveUser2.getId(), bookingCreateDto2);
        assertNotNull(saveBookingDto2.getId());
        System.out.println(bookingService.getUserBookings(saveUser1.getId(), BookingState.WAITING));
        System.out.println(bookingService.getUserBookings(saveUser2.getId(), BookingState.WAITING));
        bookingRepository.deleteById(saveBookingDto1.getId());
        bookingRepository.deleteById(saveBookingDto2.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem1.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem2.getId());
        userService.deleteUser(saveUser1.getId());
        userService.deleteUser(saveUser2.getId());
        userService.deleteUser(saveOwner.getId());
    }

    @Test
    void testBookingGetOwnerBookings() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userService.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveOwner.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveOwner.getId(), itemDto2);
        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(saveItem1.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto1 = bookingService.create(saveUser.getId(), bookingCreateDto1);
        assertNotNull(saveBookingDto1.getId());
        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(saveItem2.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto2 = bookingService.create(saveUser.getId(), bookingCreateDto2);
        assertNotNull(saveBookingDto2.getId());
        assertEquals(2, bookingService.getOwnerBookings(saveOwner.getId(), BookingState.ALL).size());
        System.out.println(bookingService.getOwnerBookings(saveOwner.getId(), BookingState.ALL));
        bookingRepository.deleteById(saveBookingDto1.getId());
        bookingRepository.deleteById(saveBookingDto2.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem1.getId());
        itemService.deleteItem(saveOwner.getId(), saveItem2.getId());
        userService.deleteUser(saveUser.getId());
        userService.deleteUser(saveOwner.getId());
    }

}
