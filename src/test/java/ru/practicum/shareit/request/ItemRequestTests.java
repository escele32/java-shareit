package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestTests {
    UserRepository userRepository;
    UserController userController;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    ItemRequestController itemRequestController;

    @Autowired
    public ItemRequestTests(UserRepository userRepository,
                            UserController userController,
                            ItemRepository itemRepository,
                            ItemRequestRepository itemRequestRepository,
                            ItemRequestController itemRequestController) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestController = itemRequestController;
    }

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testItemRequestCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна матыга!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto = itemRequestController.create(saveUserDto.getId(), itemRequestDto);
        assertNotNull(saveRequestDto.getId());
        assertEquals("Нужна матыга!", saveRequestDto.getDescription());
        System.out.println(userController.getAllUsers());
        System.out.println(itemRequestController.getOwnerRequests(saveUserDto.getId()));
    }

    @Test
    void testItemRequestById() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна матыга!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto = itemRequestController.create(saveUserDto.getId(), itemRequestDto);
        assertNotNull(saveRequestDto.getId());
        assertEquals("Нужна матыга!", saveRequestDto.getDescription());
        assertEquals(saveRequestDto.getId(),
                itemRequestController.getById(saveUserDto.getId(), saveRequestDto.getId()).getId());
        System.out.println(itemRequestController.getOwnerRequests(saveUserDto.getId()));
    }

    @Test
    void testGetOwnerRequests() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .description("Нужна матыга1!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto1 = itemRequestController.create(saveUserDto.getId(), itemRequestDto1);
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("Нужна матыга2!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto2 = itemRequestController.create(saveUserDto.getId(), itemRequestDto2);
        assertNotNull(saveRequestDto1.getId());
        assertNotNull(saveRequestDto2.getId());
        assertEquals(2, itemRequestController.getOwnerRequests(saveUserDto.getId()).size());
        System.out.println(itemRequestController.getOwnerRequests(saveUserDto.getId()));
    }

}
