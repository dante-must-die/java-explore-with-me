package ru.practicum.category.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_CATEGORY_NAME", columnNames = "name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;
}
