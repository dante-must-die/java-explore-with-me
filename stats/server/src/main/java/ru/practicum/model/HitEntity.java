package ru.practicum.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "hits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String app;

    @Column(length = 255)
    private String uri;

    @Column(length = 16)
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
