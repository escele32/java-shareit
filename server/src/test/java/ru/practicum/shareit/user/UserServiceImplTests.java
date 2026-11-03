package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
public class UserServiceImplTests {
    UserService userService;

    @Autowired
    public UserServiceImplTests(UserService userService) {
        this.userService = userService;
    }

    @Test
    void testUserCreate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        assertNotNull(saveUserDto.getId());
        assertEquals("Rafael", saveUserDto.getName());
        assertEquals("rafael@yandex.ru", saveUserDto.getEmail());
        System.out.println(userService.getAllUsers());
        UserDto badEmailUserDto = UserDto.builder()
                .name("Mariia")
                .email("mariiayndex.ru")
                .build();
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> userService.saveUser(badEmailUserDto));
        assertEquals("Некорректный email", validationException.getMessage());
        userService.deleteUser(saveUserDto.getId());
    }

    @Test
    void testUserUpdate() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        UserDto updateNameUserDto = UserDto.builder()
                .name("leafaR")
                .build();
        UserDto result = userService.updateUser(saveUserDto.getId(), updateNameUserDto);
        assertEquals("leafaR", result.getName());
        System.out.println(userService.getAllUsers());
        userService.deleteUser(result.getId());
    }

    @Test
    void testUserDelete() {
        UserDto userDto = UserDto.builder()
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        UserDto saveUserDto = userService.saveUser(userDto);
        userService.deleteUser(saveUserDto.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserId(saveUserDto.getId()));
        System.out.println(userService.getAllUsers());
    }

    @Test
    void testGetAllUsers() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUserDto1 = userService.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUserDto2 = userService.saveUser(userDto2);
        assertEquals(2, userService.getAllUsers().size());
        System.out.println(userService.getAllUsers());
        userService.deleteUser(saveUserDto1.getId());
        userService.deleteUser(saveUserDto2.getId());
    }

    @Test
    void testGetUserById() {
        UserDto userDto1 = UserDto.builder()
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto saveUserDto1 = userService.saveUser(userDto1);
        UserDto userDto2 = UserDto.builder()
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        UserDto saveUserDto2 = userService.saveUser(userDto2);
        assertNotNull(saveUserDto1.getId());
        assertNotNull(saveUserDto2.getId());
        System.out.println(userService.getAllUsers());
        System.out.println(userService.getUserId(saveUserDto2.getId()));
        userService.deleteUser(saveUserDto1.getId());
        userService.deleteUser(saveUserDto2.getId());
    }

}
