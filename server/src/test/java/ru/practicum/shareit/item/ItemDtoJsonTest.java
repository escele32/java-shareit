package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemDtoJsonTest {
    JacksonTester<ItemDto> dtoJacksonTester;

    @Test
    void setDtoJacksonTester() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Cool")
                .authorName("Mariia")
                .created(LocalDateTime.now().minusDays(2).withNano(0))
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("Матыга")
                .description("Матыга легенда!")
                .available(true)
                .comments(List.of(commentDto))
                .requestId(3L)
                .build();
        JsonContent<ItemDto> dtoJsonContent = dtoJacksonTester.write(itemDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(dtoJsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(commentDto.getCreated().toString());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(commentDto.getText());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
    }
}
