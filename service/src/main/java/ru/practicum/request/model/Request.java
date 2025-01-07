package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests",
        uniqueConstraints = {
                @UniqueConstraint(name="UQ_REQUESTER_EVENT", columnNames = {"event_id","requester_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name="event_id", nullable = false,
            foreignKey = @ForeignKey(name="FK_REQ_EVENT", value=ConstraintMode.CONSTRAINT))
    private Event event;

    @ManyToOne
    @JoinColumn(name="requester_id", nullable = false,
            foreignKey = @ForeignKey(name="FK_REQ_REQUESTER", value=ConstraintMode.CONSTRAINT))
    private User requester;

    @Column(length = 10, nullable = false)
    private String status;  // PENDING, CONFIRMED, REJECTED, CANCELED
}
