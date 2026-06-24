package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Collection<User> findUsers();

    Optional<User> findUserById(Long userId);

    void delete(Long userId);

    User update(User user);

    boolean existsEmail(String email);

    void clean();
}
