package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public User save(User user) {
        user.setId(atomicLong.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findUsers() {
        return users.values();
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public void clean() {
        users.clear();
    }
}
