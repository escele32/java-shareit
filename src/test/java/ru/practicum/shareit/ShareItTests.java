package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ShareItTests {
	UserRepository userRepository;
	ItemRepository itemRepository;
	UserController userController;
	ItemController itemController;

	@Autowired
    ShareItTests(UserRepository userRepository, ItemRepository itemRepository,
                 UserController userController, ItemController itemController) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userController = userController;
        this.itemController = itemController;
    }

	@BeforeEach
	void setup() {
		userRepository.clean();
		itemRepository.clean();
	}

	static User user = User.builder()
				.name("user")
				.email("user@email.ru")
				.build();
	static User other = User.builder()
				.name("other")
				.email("other@email.ru")
				.build();
	static User noValidEmailUser = User.builder()
				.name("noValidEmailUser")
				.email("noValidEmailUseremail.ru")
				.build();
	static Item itemUser2 = Item.builder()
				.name("itemUser2")
				.description("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
				.available(true)
				.build();
	static Item itemUser1 = Item.builder()
				.name("itemUser1")
				.description("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
				.available(false)
				.build();
	static Item itemOther1 = Item.builder()
				.name("itemOther1")
				.description("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC")
				.available(true)
				.build();
	static Item itemOther2 = Item.builder()
				.name("itemOther2")
				.description("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
				.available(false)
				.build();

	@Test
	void testCreateUser() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		ValidationException validationException = assertThrows(ValidationException.class,
				() -> userController.saveUser(UserMapper.toUserDto(noValidEmailUser)));
		assertEquals("Некорректный email", validationException.getMessage());
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testCreateItem() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		itemController.saveItem( 1L, ItemMapper.toItemDto(itemUser1));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemUser2));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther1));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther2));
		System.out.println(itemController.getOwnerItems(1L));
		System.out.println(itemController.getOwnerItems(2L));
	}

	@Test
	void testUpdateUser() {
		userController.saveUser(UserMapper.toUserDto(user));
		System.out.println(userController.getAllUsers());
		User updateUser = User.builder()
				.name("updateUser")
				.build();
		userController.updateUser(1L, UserMapper.toUserDto(updateUser));
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testUpdateItem() {
		userController.saveUser(UserMapper.toUserDto(other));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemOther1));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemOther2));
		System.out.println(userController.getAllUsers());
		System.out.println(itemController.getOwnerItems(1L));
		Item updateItemOther2 = Item.builder()
				.name("updateItemOther2")
				.build();
		itemController.updateItem(1L, 2L, ItemMapper.toItemDto(updateItemOther2));
		System.out.println(itemController.getOwnerItems(1L));
	}

	@Test
	void testGetAllUsers() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testGetAllOwnerItems() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		itemController.saveItem( 1L, ItemMapper.toItemDto(itemUser1));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemUser2));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther1));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther2));
		System.out.println(itemController.getOwnerItems(1L));
		System.out.println(itemController.getOwnerItems(2L));
	}

	@Test
	void testGetUserById() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		System.out.println(userController.getUserById(2L));
	}

	@Test
	void testGetItemById() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		itemController.saveItem( 1L, ItemMapper.toItemDto(itemUser1));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemUser2));
		System.out.println(itemController.getOwnerItems(1L));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther1));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther2));
		System.out.println(itemController.getOwnerItems(2L));
		System.out.println(itemController.getItemById(1L, 2L));
	}

	@Test
	void testDeleteUserById() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		userController.deleteUser(2L);
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testDeleteItemById() {
		userController.saveUser(UserMapper.toUserDto(user));
		userController.saveUser(UserMapper.toUserDto(other));
		System.out.println(userController.getAllUsers());
		itemController.saveItem( 1L, ItemMapper.toItemDto(itemUser1));
		itemController.saveItem(1L, ItemMapper.toItemDto(itemUser2));
		System.out.println(itemController.getOwnerItems(1L));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther1));
		itemController.saveItem(2L, ItemMapper.toItemDto(itemOther2));
		System.out.println(itemController.getOwnerItems(2L));
		itemController.deleteItem(2L, 4L);
		System.out.println(itemController.getOwnerItems(2L));
		ValidationException validationException = assertThrows(ValidationException.class,
				() -> itemController.deleteItem(2L, 2L));
		assertEquals("Нельзя удалять чужую вещь", validationException.getMessage());
	}
}
