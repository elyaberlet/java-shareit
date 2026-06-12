package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
