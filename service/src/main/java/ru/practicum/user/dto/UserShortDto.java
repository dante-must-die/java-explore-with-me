package ru.practicum.user.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    private Long id;
    @Size(min = 2, max = 250)
    private String name;
}
