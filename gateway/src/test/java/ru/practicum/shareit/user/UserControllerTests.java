package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ApiPath;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTests {
    @MockBean
    UserClient userClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Rafael")
            .email("rafael@yandex.ru")
            .build();

    @Test
    void testUserCreate() throws Exception {
        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok().body(userDto));
        mockMvc.perform(post(ApiPath.USERS)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));;
        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void testUserUpdate() throws Exception {
        UserDto updateUserDto = UserDto.builder()
                .name("leafaR")
                .build();
        userDto.setName(updateUserDto.getName());
        when(userClient.update(anyLong(), any(UserDto.class))).thenReturn(ResponseEntity.ok().body(userDto));
        mockMvc.perform(patch(ApiPath.USERS + ApiPath.USERID, userDto.getId())
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userClient).update(anyLong(), any(UserDto.class));
    }

    @Test
    void testUserById() throws Exception {
        when(userClient.getById(anyLong())).thenReturn(ResponseEntity.ok().body(userDto));
        mockMvc.perform(get(ApiPath.USERS + ApiPath.USERID, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userClient).getById(anyLong());
    }

    @Test
    void testAllUsers() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("Rafael1")
                .email("rafael1@yandex.ru")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("Rafael2")
                .email("rafael2@yandex.ru")
                .build();
        when(userClient.getAll()).thenReturn(ResponseEntity.ok().body(List.of(userDto1, userDto2)));
        mockMvc.perform(get(ApiPath.USERS)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
        verify(userClient).getAll();
    }

    @Test
    void testUserDelete() throws Exception {
        when(userClient.delete(anyLong())).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(delete(ApiPath.USERS + ApiPath.USERID, userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userClient).delete(anyLong());
    }
}