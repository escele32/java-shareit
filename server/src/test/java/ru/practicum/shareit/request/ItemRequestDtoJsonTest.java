package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestDtoJsonTest {
    JacksonTester<ItemRequestDto> dtoJacksonTester;

    @Test
    void setDtoJacksonTester() throws Exception {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .name("SSS")
                .ownerId(2L)
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(3L)
                .description("asdasdasdasd")
                .created(LocalDateTime.now().minusDays(2).withNano(0))
                .items(List.of(requestDto))
                .build();
        JsonContent<ItemRequestDto> dtoJsonContent = dtoJacksonTester.write(itemRequestDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(requestDto.getName());
    }
}
