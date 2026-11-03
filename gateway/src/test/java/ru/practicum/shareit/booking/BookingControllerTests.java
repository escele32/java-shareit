package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ApiPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTests {
    @MockBean
    BookingClient bookingClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    BookItemRequestDto bookItemRequestDto = BookItemRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1).withNano(0))
            .end(LocalDateTime.now().plusDays(4).withNano(0))
            .build();

    @Test
    void testBookingCreate() throws Exception {
        when(bookingClient.bookItem(anyLong(), any())).thenReturn(ResponseEntity.ok().body(bookItemRequestDto));
        mockMvc.perform(post(ApiPath.BOOKINGS)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookItemRequestDto.getEnd().toString())));
        verify(bookingClient).bookItem(anyLong(), any());
    }

    @Test
    void testBookingApprove() throws Exception {
        when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().body(bookItemRequestDto));
        mockMvc.perform(patch(ApiPath.BOOKINGS + ApiPath.BOOKINGID, 2)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookItemRequestDto.getEnd().toString())));
        verify(bookingClient).approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void testBookingById() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().body(bookItemRequestDto));
        mockMvc.perform(get(ApiPath.BOOKINGS + ApiPath.BOOKINGID, 8)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookItemRequestDto.getEnd().toString())));
        verify(bookingClient).getBooking(anyLong(), anyLong());
    }

    @Test
    void testBookingUser() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(List.of(bookItemRequestDto)));
        mockMvc.perform(get(ApiPath.BOOKINGS)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 7)
                        .param("state", String.valueOf(BookingState.ALL))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookItemRequestDto.getEnd().toString())));
        verify(bookingClient).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void testBookingOwner() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().body(List.of(bookItemRequestDto)));
        mockMvc.perform(get(ApiPath.BOOKINGS + ApiPath.OWNER)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ApiPath.HEADER, 10)
                        .param("state", String.valueOf(BookingState.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookItemRequestDto.getEnd().toString())));
        verify(bookingClient).getOwnerBookings(anyLong(), any());
    }
}
