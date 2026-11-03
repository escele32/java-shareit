package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImplTests {
    UserService userService;
    ItemService itemService;

    @Autowired
    public ItemServiceImplTests(UserService userService,
                                ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @Test
    void testItemCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemService.saveItem(saveUser.getId(), itemDto);
        assertNotNull(saveItem.getId());
        assertEquals("Матыга", saveItem.getName());
        assertEquals("Матыга легенда!", saveItem.getDescription());
        assertTrue(saveItem.getAvailable());
        System.out.println(itemService.getOwnerItems(saveUser.getId()));
        itemService.deleteItem(saveUser.getId(), saveItem.getId());
        userService.deleteUser(saveUser.getId());
    }

    @Test
    void testItemUpdate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemService.saveItem(saveUser.getId(), itemDto);
        ItemDto updateItemDescription = ItemDto.builder()
                .description("Не может быть!")
                .build();
        ItemDto updateItem = itemService.updateItem(saveUser.getId(), saveItem.getId(), updateItemDescription);
        assertEquals("Не может быть!", updateItem.getDescription());
        System.out.println(itemService.getOwnerItems(saveUser.getId()));
        itemService.deleteItem(saveUser.getId(), updateItem.getId());
        userService.deleteUser(saveUser.getId());
    }

    @Test
    void testItemById() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        System.out.println(itemService.getOwnerItems(saveUser.getId()));
        System.out.println(itemService.getItemId(saveItem1.getId(), saveItem2.getId()));
        itemService.deleteItem(saveUser.getId(), saveItem1.getId());
        itemService.deleteItem(saveUser.getId(), saveItem2.getId());
        userService.deleteUser(saveUser.getId());
    }

    @Test
    void testGetOwnerItems() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUser1 = userService.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUser2 = userService.saveUser(userDto2);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveUser1.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveUser1.getId(), itemDto2);
        assertNotNull(saveUser1.getId());
        assertNotNull(saveUser2.getId());
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        assertEquals(0, itemService.getOwnerItems(saveUser2.getId()).size());
        assertEquals(2, itemService.getOwnerItems(saveUser1.getId()).size());
        System.out.println(itemService.getOwnerItems(saveUser1.getId()));
        System.out.println(itemService.getOwnerItems(saveUser2.getId()));
        itemService.deleteItem(saveUser1.getId(), saveItem1.getId());
        itemService.deleteItem(saveUser1.getId(), saveItem2.getId());
        userService.deleteUser(saveUser1.getId());
        userService.deleteUser(saveUser2.getId());
    }

    @Test
    void testItemDelete() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        itemService.deleteItem(saveUser.getId(), saveItem1.getId());
        assertThrows(NotFoundException.class, () -> itemService.getItemId(saveUser.getId(), saveItem1.getId()));
        System.out.println(itemService.getOwnerItems(saveUser.getId()));
        itemService.deleteItem(saveUser.getId(), saveItem2.getId());
        userService.deleteUser(saveUser.getId());
    }

    @Test
    void testItemSearch() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userService.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemService.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 Легенда2!")
                .available(false)
                .build();
        ItemDto saveItem2 = itemService.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        Collection<ItemDto> search = itemService.search(saveUser.getId(), "лег");
        assertEquals(1, search.size());
        System.out.println(itemService.getOwnerItems(saveUser.getId()));
        System.out.println(search);
        itemService.deleteItem(saveUser.getId(), saveItem1.getId());
        itemService.deleteItem(saveUser.getId(), saveItem2.getId());
        userService.deleteUser(saveUser.getId());
    }

}
