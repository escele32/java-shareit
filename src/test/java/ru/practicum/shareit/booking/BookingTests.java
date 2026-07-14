package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingTests {
    BookingRepository bookingRepository;
    BookingController bookingController;
    ItemRepository itemRepository;
    ItemController itemController;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    UserController userController;

    @Autowired
    public BookingTests(BookingRepository bookingRepository,
                        BookingController bookingController,
                        ItemRepository itemRepository,
                        ItemController itemController,
                        ItemRequestRepository itemRequestRepository,
                        UserRepository userRepository,
                        UserController userController) {
        this.bookingRepository = bookingRepository;
        this.bookingController = bookingController;
        this.itemRepository = itemRepository;
        this.itemController = itemController;
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.userController = userController;
    }

    @BeforeEach
    void setup() {
        itemRequestRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testBookingCreate() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userController.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemController.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingController.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        assertEquals(saveItem.getId(), saveBookingDto.getItem().getId());
        assertEquals(saveUser.getId(), saveBookingDto.getBooker().getId());
        assertEquals(BookingStatus.WAITING, saveBookingDto.getStatus());
        System.out.println(bookingController.getUserBookings(saveUser.getId(), BookingState.WAITING));
    }

    @Test
    void testBookingGetById() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userController.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemController.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingController.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        assertEquals( saveBookingDto.getId(),
                bookingController.getById(saveUser.getId(), saveBookingDto.getId()).getId());
        System.out.println(bookingController.getById(saveUser.getId(), saveBookingDto.getId()));
    }

    @Test
    void testBookingApprove() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userController.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemController.saveItem(saveOwner.getId(), itemDto);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(saveItem.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto = bookingController.create(saveUser.getId(), bookingCreateDto);
        assertNotNull(saveBookingDto.getId());
        BookingDto approveBookingDto = bookingController.approve(saveOwner.getId(),
                saveBookingDto.getId(), true);
        assertEquals(BookingStatus.APPROVED, approveBookingDto.getStatus());
        System.out.println(bookingController.getOwnerBookings(saveOwner.getId(), BookingState.CURRENT));
    }

    @Test
    void testBookingGetUserBookings() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userController.saveUser(ownerDto);
        UserDto userDto1 = UserDto.builder()
                .name("Mariia1")
                .email("mariia1@yandex.ru")
                .build();
        UserDto saveUser1 = userController.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Mariia2")
                .email("mariia2@yandex.ru")
                .build();
        UserDto saveUser2 = userController.saveUser(userDto2);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveOwner.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveOwner.getId(), itemDto2);
        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(saveItem1.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto1 = bookingController.create(saveUser1.getId(), bookingCreateDto1);
        assertNotNull(saveBookingDto1.getId());
        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(saveItem2.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto2 = bookingController.create(saveUser2.getId(), bookingCreateDto2);
        assertNotNull(saveBookingDto2.getId());
        System.out.println(bookingController.getUserBookings(saveUser1.getId(), BookingState.WAITING));
        System.out.println(bookingController.getUserBookings(saveUser2.getId(), BookingState.WAITING));
    }

    @Test
    void testBookingGetOwnerBookings() {
        UserDto ownerDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveOwner = userController.saveUser(ownerDto);
        UserDto userDto = UserDto.builder()
                .name("Mariia")
                .email("mariia@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveOwner.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveOwner.getId(), itemDto2);
        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(saveItem1.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto1 = bookingController.create(saveUser.getId(), bookingCreateDto1);
        assertNotNull(saveBookingDto1.getId());
        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(saveItem2.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        BookingDto saveBookingDto2 = bookingController.create(saveUser.getId(), bookingCreateDto2);
        assertNotNull(saveBookingDto2.getId());
        assertEquals(2, bookingController.getOwnerBookings(saveOwner.getId(), BookingState.ALL).size());
        System.out.println(bookingController.getOwnerBookings(saveOwner.getId(), BookingState.ALL));
    }

}
