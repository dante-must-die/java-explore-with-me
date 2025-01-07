package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @Email
    @NotBlank
    @Length(min = 6, max = 254)
    private String email;

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
