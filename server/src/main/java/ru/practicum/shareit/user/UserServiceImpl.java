package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User validationUserId(Long userId) {
        if (userId == null) {
            log.warn("Id пользователя не может быть null");
            throw new ValidationException("Id пользователя не может быть null");
        }
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException(format("Пользователь с id %d не найден\n", userId));
        });
    }

    private void validateEmail(String email) {
        if (email.isBlank()) {
            log.error("Email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }

        if (!email.contains("@")) {
            log.error("Некорректный email");
            throw new ValidationException("Некорректный email");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение списка пользователей");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        User user = validationUserId(userId);
        userRepository.deleteById(user.getId());
    }

    @Override
    public Optional<UserDto> getUserId(Long userId) {
        log.info("Получение пользователя с id {}", userId);
        User user = validationUserId(userId);
        return userRepository.findById(user.getId())
                .stream()
                .map(UserMapper::toUserDto)
                .findFirst();
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Создание пользователя {}", userDto);
        validateEmail(userDto.getEmail());
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} уже используется", userDto.getEmail());
            throw new IllegalArgumentException(format("Email %s уже используется", userDto.getEmail()));
        }
        User user = UserMapper.toUser(userDto);
        User saveUser = userRepository.save(user);
        return UserMapper.toUserDto(saveUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Обновление пользователя {} с id {}", userDto, userId);
        User user = validationUserId(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), user.getId())) {
                log.warn("Email {} уже используется", userDto.getEmail());
                throw new IllegalArgumentException(format("Email %s уже используется", userDto.getEmail()));
            }
            user.setEmail(userDto.getEmail());
        }
        User saveUser = userRepository.save(user);
        return UserMapper.toUserDto(saveUser);
    }
}