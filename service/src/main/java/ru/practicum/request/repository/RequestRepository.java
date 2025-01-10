package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequester_Id(Long userId);

    List<Request> findByEvent_Id(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

}
