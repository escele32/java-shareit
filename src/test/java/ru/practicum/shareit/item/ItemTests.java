package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemTests {
    ItemRepository itemRepository;
    ItemController itemController;
    UserRepository userRepository;
    UserController userController;

    @Autowired
    public ItemTests(ItemRepository itemRepository,
                     ItemController itemController,
                     UserRepository userRepository,
                     UserController userController) {
        this.itemRepository = itemRepository;
        this.itemController = itemController;
        this.userRepository = userRepository;
        this.userController = userController;
    }

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testItemCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemController.saveItem(saveUser.getId(), itemDto);
        assertNotNull(saveItem.getId());
        assertEquals("Матыга", saveItem.getName());
        assertEquals("Матыга легенда!", saveItem.getDescription());
        assertTrue(saveItem.getAvailable());
        System.out.println(itemController.getOwnerItems(saveUser.getId()));
    }

    @Test
    void testItemUpdate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto = ItemDto.builder()
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .build();
        ItemDto saveItem = itemController.saveItem(saveUser.getId(), itemDto);
        ItemDto updateItemDescription = ItemDto.builder()
                .description("Не может быть!")
                .build();
        ItemDto updateItem = itemController.updateItem(saveUser.getId(), saveItem.getId(), updateItemDescription);
        assertEquals("Не может быть!", updateItem.getDescription());
        System.out.println(itemController.getOwnerItems(saveUser.getId()));
    }

    @Test
    void testItemById() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        System.out.println(itemController.getOwnerItems(saveUser.getId()));
        System.out.println(itemController.getItemById(saveItem1.getId(), saveItem2.getId()));
    }

    @Test
    void testGetOwnerItems() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUser1 = userController.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUser2 = userController.saveUser(userDto2);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveUser1.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveUser1.getId(), itemDto2);
        assertNotNull(saveUser1.getId());
        assertNotNull(saveUser2.getId());
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        assertEquals(0, itemController.getOwnerItems(saveUser2.getId()).size());
        assertEquals(2, itemController.getOwnerItems(saveUser1.getId()).size());
        System.out.println(itemController.getOwnerItems(saveUser1.getId()));
        System.out.println(itemController.getOwnerItems(saveUser2.getId()));
    }

    @Test
    void testItemDelete() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 легенда2!")
                .available(true)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        itemController.deleteItem(saveUser.getId(), saveItem1.getId());
        assertThrows(NotFoundException.class, () -> itemController.getItemById(saveUser.getId(), saveItem1.getId()));
        System.out.println(itemController.getOwnerItems(saveUser.getId()));
    }

    @Test
    void testItemSearch() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUser = userController.saveUser(userDto);
        ItemDto itemDto1 = ItemDto.builder()
                .name("Матыга1")
                .description("Матыга1 легенда1!")
                .available(true)
                .build();
        ItemDto saveItem1 = itemController.saveItem(saveUser.getId(), itemDto1);
        ItemDto itemDto2 = ItemDto.builder()
                .name("Матыга2")
                .description("Матыга2 Легенда2!")
                .available(false)
                .build();
        ItemDto saveItem2 = itemController.saveItem(saveUser.getId(), itemDto2);
        assertNotNull(saveItem1.getId());
        assertNotNull(saveItem2.getId());
        Collection<ItemDto> search = itemController.search(saveUser.getId(), "лег");
        assertEquals(1, search.size());
        System.out.println(itemController.getOwnerItems(saveUser.getId()));
        System.out.println(search);
    }

}
