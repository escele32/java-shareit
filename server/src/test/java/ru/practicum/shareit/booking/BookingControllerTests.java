package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ApiPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTests {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    UserDto booker = UserDto.builder()
            .id(3L)
            .name("Mariia")
            .email("m@ya.ru")
            .build();
    BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2026, 1, 1, 22, 0, 1))
            .end(LocalDateTime.of(2026, 2, 1, 22, 0, 1))
            .build();
    BookingItemDto bookingItemDto = BookingItemDto.builder()
            .id(bookingCreateDto.getItemId())
            .name("Knife")
            .build();
    BookingDto bookingDto = BookingDto.builder()
            .id(2L)
            .start(bookingCreateDto.getStart())
            .end(bookingCreateDto.getEnd())
            .item(bookingItemDto)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void testBookingCreate() throws Exception {
        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);
        mockMvc.perform(post(ApiPath.BOOKINGS)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testBookingApprove() throws Exception {
        bookingDto.setStatus(BookingStatus.WAITING);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        mockMvc.perform(patch(ApiPath.BOOKINGS + ApiPath.BOOKINGID, bookingDto.getId())
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(get(ApiPath.BOOKINGS + ApiPath.BOOKINGID, bookingDto.getId())
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testBookingUser() throws Exception {
        when(bookingService.getUserBookings(anyLong(), any())).thenReturn(List.of(bookingDto));
        mockMvc.perform(get(ApiPath.BOOKINGS)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 7)
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testBookingOwner() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), any())).thenReturn(List.of(bookingDto));
        mockMvc.perform(get(ApiPath.BOOKINGS + ApiPath.OWNER)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 10)
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookingDto.getBooker().getEmail())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }
}
