package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDtoJsonTest {
    JacksonTester<UserDto> dtoJacksonTester;

    @Test
    void setDtoJacksonTester() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Rafael")
                .email("rafael@yandex.ru")
                .build();
        JsonContent<UserDto> dtoJsonContent = dtoJacksonTester.write(userDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
