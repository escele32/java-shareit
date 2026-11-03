package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImplTests {
    UserService userService;
    ItemRequestService itemRequestService;
    ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImplTests(UserService userService,
                                       ItemRequestService itemRequestService,
                                       ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRequestService = itemRequestService;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Test
    void testItemRequestCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна матыга!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto = itemRequestService.create(saveUserDto.getId(), itemRequestDto);
        assertNotNull(saveRequestDto.getId());
        assertEquals("Нужна матыга!", saveRequestDto.getDescription());
        System.out.println(userService.getAllUsers());
        System.out.println(itemRequestService.getOwnerRequests(saveUserDto.getId()));
        itemRequestRepository.deleteById(saveRequestDto.getId());
        userService.deleteUser(saveUserDto.getId());
    }

    @Test
    void testItemRequestById() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна матыга!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto = itemRequestService.create(saveUserDto.getId(), itemRequestDto);
        assertNotNull(saveRequestDto.getId());
        assertEquals("Нужна матыга!", saveRequestDto.getDescription());
        assertEquals(saveRequestDto.getId(),
                itemRequestService.getById(saveUserDto.getId(), saveRequestDto.getId()).getId());
        System.out.println(itemRequestService.getOwnerRequests(saveUserDto.getId()));
        itemRequestRepository.deleteById(saveRequestDto.getId());
        userService.deleteUser(saveUserDto.getId());
    }

    @Test
    void testGetOwnerRequests() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .description("Нужна матыга1!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto1 = itemRequestService.create(saveUserDto.getId(), itemRequestDto1);
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("Нужна матыга2!")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto saveRequestDto2 = itemRequestService.create(saveUserDto.getId(), itemRequestDto2);
        assertNotNull(saveRequestDto1.getId());
        assertNotNull(saveRequestDto2.getId());
        assertEquals(2, itemRequestService.getOwnerRequests(saveUserDto.getId()).size());
        System.out.println(itemRequestService.getOwnerRequests(saveUserDto.getId()));
        itemRequestRepository.deleteById(saveRequestDto1.getId());
        itemRequestRepository.deleteById(saveRequestDto2.getId());
        userService.deleteUser(saveUserDto.getId());
    }

}
