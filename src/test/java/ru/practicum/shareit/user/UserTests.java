package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserTests {
    UserRepository userRepository;
    UserController userController;

    @Autowired
    public UserTests(UserRepository userRepository, UserController userController) {
        this.userRepository = userRepository;
        this.userController = userController;
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testUserCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        assertNotNull(saveUserDto.getId());
        assertEquals("Rafael", saveUserDto.getName());
        assertEquals("rafael@yandex.ru", saveUserDto.getEmail());
        System.out.println(userController.getAllUsers());
        UserDto badEmailUserDto = UserDto.builder()
                .name("Mariia")
                .email("mariiayndex.ru")
                .build();
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> userController.saveUser(badEmailUserDto));
        assertEquals("Некорректный email", validationException.getMessage());
    }

    @Test
    void testUserUpdate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        UserDto updateNameUserDto = UserDto.builder()
                .name("leafaR")
                .build();
        UserDto result = userController.updateUser(saveUserDto.getId(), updateNameUserDto);
        assertEquals("leafaR", result.getName());
        System.out.println(userController.getAllUsers());
    }

    @Test
    void testUserDelete() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userController.saveUser(userDto);
        userController.deleteUser(saveUserDto.getId());
        assertThrows(NotFoundException.class, () -> userController.getUserById(saveUserDto.getId()));
        System.out.println(userController.getAllUsers());
    }

    @Test
    void testGetAllUsers() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUserDto1 = userController.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUserDto2 = userController.saveUser(userDto2);
        assertEquals(2, userController.getAllUsers().size());
        System.out.println(userController.getAllUsers());
    }

    @Test
    void testGetUserById() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUserDto1 = userController.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUserDto2 = userController.saveUser(userDto2);
        assertNotNull(saveUserDto1.getId());
        assertNotNull(saveUserDto2.getId());
        System.out.println(userController.getAllUsers());
        System.out.println(userController.getUserById(saveUserDto2.getId()));
    }

}
