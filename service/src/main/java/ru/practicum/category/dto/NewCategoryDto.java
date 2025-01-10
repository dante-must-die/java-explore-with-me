package ru.practicum.category.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Category name must not be blank")
    @Length(max = 50)
    private String name;
}
