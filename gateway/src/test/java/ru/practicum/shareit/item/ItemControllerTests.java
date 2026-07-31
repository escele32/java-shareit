package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.ApiPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTests {
    @MockBean
    ItemClient itemClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("asdasdasd")
            .authorName("Mariia")
            .created(LocalDateTime.of(1987,1,16,21,30, 20))
            .build();
    ItemDto itemDto = ItemDto.builder()
            .id(2L)
            .name("Knife")
            .description("Cool")
            .available(true)
            .lastBooking(LocalDateTime.of(1986,1,16,21,30, 20))
            .nextBooking(LocalDateTime.of(1988,1,16,21,30, 20))
            .comments(List.of(commentDto))
            .requestId(3L)
            .build();

    @Test
    void testItemSave() throws Exception {
        when(itemClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().body(itemDto));
        mockMvc.perform(post(ApiPath.ITEMS)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking().toString())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking().toString())))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("asdasdasd")))
                .andExpect(jsonPath("$.comments[0].authorName", is("Mariia")))
                .andExpect(jsonPath("$.comments[0].created", is("1987-01-16T21:30:20")))
                .andExpect(jsonPath("$.requestId", is(3)));
        verify(itemClient).create(anyLong(), any());
    }

    @Test
    void testItemUpdate() throws Exception {
        ItemDto updateNameItemDto = ItemDto.builder()
                .name("efinK")
                .build();
        itemDto.setName(updateNameItemDto.getName());
        when(itemClient.update(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(itemDto));
        mockMvc.perform(patch(ApiPath.ITEMS + ApiPath.ITEMID, itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("efinK")))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking().toString())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking().toString())))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("asdasdasd")))
                .andExpect(jsonPath("$.comments[0].authorName", is("Mariia")))
                .andExpect(jsonPath("$.comments[0].created", is("1987-01-16T21:30:20")))
                .andExpect(jsonPath("$.requestId", is(3)));
        verify(itemClient).update(anyLong(), anyLong(), any());
    }

    @Test
    void testItemGetById() throws Exception {
        when(itemClient.getById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().body(itemDto));
        mockMvc.perform(get(ApiPath.ITEMS + ApiPath.ITEMID, itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking().toString())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking().toString())))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("asdasdasd")))
                .andExpect(jsonPath("$.comments[0].authorName", is("Mariia")))
                .andExpect(jsonPath("$.comments[0].created", is("1987-01-16T21:30:20")))
                .andExpect(jsonPath("$.requestId", is(3)));
        verify(itemClient).getById(anyLong(), anyLong());
    }

    @Test
    void testItemSearch() throws Exception {
        when(itemClient.search(anyLong(), anyString())).thenReturn(ResponseEntity.ok().body(List.of(itemDto)));
        mockMvc.perform(get(ApiPath.ITEMS + ApiPath.SEARCH)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4)
                        .queryParam("text", "asd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemDto.getLastBooking().toString())))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemDto.getNextBooking().toString())))
                .andExpect(jsonPath("$.[0].comments[0].id", is(1)))
                .andExpect(jsonPath("$.[0].comments[0].text", is("asdasdasd")))
                .andExpect(jsonPath("$.[0].comments[0].authorName", is("Mariia")))
                .andExpect(jsonPath("$.[0].comments[0].created", is("1987-01-16T21:30:20")))
                .andExpect(jsonPath("$.[0].requestId", is(3)));
        verify(itemClient).search(anyLong(), anyString());
    }

    @Test
    void testItemOwner() throws Exception {
        when(itemClient.getOwnerItems(anyLong())).thenReturn(ResponseEntity.ok().body(List.of(itemDto)));
        mockMvc.perform(get(ApiPath.ITEMS)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemDto.getLastBooking().toString())))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemDto.getNextBooking().toString())))
                .andExpect(jsonPath("$.[0].comments[0].id", is(1)))
                .andExpect(jsonPath("$.[0].comments[0].text", is("asdasdasd")))
                .andExpect(jsonPath("$.[0].comments[0].authorName", is("Mariia")))
                .andExpect(jsonPath("$.[0].comments[0].created", is("1987-01-16T21:30:20")))
                .andExpect(jsonPath("$.[0].requestId", is(3)));
        verify(itemClient).getOwnerItems(anyLong());
    }

    @Test
    void testItemAddComment() throws Exception {
        ItemDto itemDto1 = ItemDto.builder()
                .id(5L)
                .name("Qwerty")
                .description("zxczxc")
                .available(false)
                .build();
        when(itemClient.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(commentDto));
        mockMvc.perform(post(ApiPath.ITEMS + ApiPath.ITEMIDCOMMENT, itemDto1.getId())
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
        verify(itemClient).addComment(anyLong(), anyLong(), any());
    }

    @Test
    void testItemDelete() throws Exception {
        mockMvc.perform(delete(ApiPath.ITEMS + ApiPath.ITEMID, itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 4))
                .andExpect(status().isOk());
    }
}