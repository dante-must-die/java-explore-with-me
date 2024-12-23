package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EndpointHitEntity;

public interface EndpointHitRepository extends JpaRepository<EndpointHitEntity, Long> {

}
