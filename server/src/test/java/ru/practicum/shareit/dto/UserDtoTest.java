package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testUserCreateDto() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Анна");
        dto.setEmail("anna@mail.com");

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Анна\"");
        assertThat(json).contains("\"email\":\"anna@mail.com\"");

        UserCreateDto result = mapper.readValue(json, UserCreateDto.class);
        assertThat(result.getName()).isEqualTo("Анна");
        assertThat(result.getEmail()).isEqualTo("anna@mail.com");
    }

    @Test
    void testUserResponseDto() throws Exception {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setName("Иван");
        dto.setEmail("ivan@mail.com");

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Иван\"");
        assertThat(json).contains("\"email\":\"ivan@mail.com\"");

        UserResponseDto result = mapper.readValue(json, UserResponseDto.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Иван");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.com");
    }

    @Test
    void testUserUpdateDto() throws Exception {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Пётр");
        dto.setEmail("petr@mail.com");

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Пётр\"");
        assertThat(json).contains("\"email\":\"petr@mail.com\"");

        UserUpdateDto result = mapper.readValue(json, UserUpdateDto.class);
        assertThat(result.getName()).isEqualTo("Пётр");
        assertThat(result.getEmail()).isEqualTo("petr@mail.com");
    }
}