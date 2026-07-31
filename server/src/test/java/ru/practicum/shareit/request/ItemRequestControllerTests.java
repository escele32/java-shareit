package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.util.ApiPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTests {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    final RequestDto requestDto = RequestDto.builder()
            .id(1L)
            .name("Rafael")
            .ownerId(2L)
            .build();
    final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(3L)
            .description("asdasdasd")
            .created(LocalDateTime.now().minusDays(2).withNano(0))
            .items(List.of(requestDto))
            .build();

    @Test
    void testItemRequestCreate() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto);
        mockMvc.perform(post(ApiPath.REQUESTS)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(requestDto.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(2)));
    }

    @Test
    void testItemRequestGetById() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get(ApiPath.REQUESTS + ApiPath.REQUESTID, requestDto.getOwnerId(),
                itemRequestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 2))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Rafael")))
                .andExpect(jsonPath("$.items[0].ownerId", is(2)));
    }

    @Test
    void testItemRequestGetOwnerRequests() throws Exception {
        when(itemRequestService.getOwnerRequests(anyLong())).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get(ApiPath.REQUESTS)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Rafael")))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(2)));
    }

    @Test
    void testItemRequestGetAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get(ApiPath.REQUESTS + ApiPath.ALL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 2)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Rafael")))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(2)));
    }
}