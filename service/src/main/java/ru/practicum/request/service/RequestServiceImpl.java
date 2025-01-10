package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.enum_.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        return requestRepository.findByRequester_Id(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        // 1) Нельзя дублировать запрос (unique)
        if (requestRepository.findByEvent_Id(eventId).stream()
                .anyMatch(r -> r.getRequester().getId().equals(userId))) {
            throw new ConflictException("Request already exists for this user and event");
        }
        // 2) Инициатор события не может отправлять запрос
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator can't request participation in own event");
        }
        // 3) Событие должно быть PUBLISHED
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event " + eventId + " not published");
        }
        // 4) Проверяем лимит
        long limit = (event.getParticipantLimit() == null ? 0 : event.getParticipantLimit());
        if (limit > 0) {
            long confirmed = (event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests());
            if (confirmed >= limit) {
                throw new ConflictException("The participant limit has been reached");
            }
        }

        // Создаём запрос
        Request r = new Request();
        r.setCreated(LocalDateTime.now());
        r.setEvent(event);
        r.setRequester(user);

        // Автоконфирм, если requestModeration = false или limit=0
        if (!event.getRequestModeration() || limit == 0) {
            r.setStatus("CONFIRMED");
            // event.confirmedRequests++
            event.setConfirmedRequests(
                    (event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests()) + 1
            );
            eventRepository.save(event);
        } else {
            r.setStatus("PENDING");
        }

        Request saved = requestRepository.save(r);
        return RequestMapper.toDto(saved);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request r = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request " + requestId + " not found"));
        if (!r.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request " + requestId + " is not owned by user " + userId);
        }
        // если он был CONFIRMED -> уменьшаем счётчик confirmedRequests
        if ("CONFIRMED".equals(r.getStatus())) {
            Event event = r.getEvent();
            long current = (event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests());
            if (current > 0) {
                event.setConfirmedRequests(current - 1);
                eventRepository.save(event);
            }
        }

        r.setStatus("CANCELED");
        Request updated = requestRepository.save(r);
        return RequestMapper.toDto(updated);
    }

    /**
     * Изменение статуса (подтверждение/отклонение) заявок на участие в событии (инициатором).
     */
    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        // Находим событие
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        // Проверяем, что userId == initiator
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User " + userId + " is not the initiator of event " + eventId);
        }

        // Собираем все нужные заявки
        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        if (requests.isEmpty()) {
            // Можно либо вернуть пустой результат, либо кинуть NotFound,
            // зависит от требований. Пусть будет пустой результат:
            return new EventRequestStatusUpdateResult(
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        // Подтверждённые / отклонённые
        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        // Берём лимит
        long limit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();
        long confirmedAlready = event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests();

        // Обрабатываем
        String newStatus = updateRequest.getStatus();  // "CONFIRMED" или "REJECTED"
        switch (newStatus) {
            case "CONFIRMED":
                for (Request r : requests) {
                    if (!"PENDING".equals(r.getStatus())) {
                        throw new ConflictException("Request must have status PENDING");
                    }
                    // Проверяем лимит
                    if (limit > 0 && confirmedAlready >= limit) {
                        // Лимит достигнут -> отклоняем все остальные
                        throw new ConflictException("The participant limit has been reached");
                    } else {
                        // Подтверждаем
                        r.setStatus("CONFIRMED");
                        requestRepository.save(r);
                        confirmedList.add(r);
                        confirmedAlready++;
                    }
                }
                // Сохраняем новое значение confirmedRequests в event
                event.setConfirmedRequests(confirmedAlready);
                eventRepository.save(event);
                break;

            case "REJECTED":
                for (Request r : requests) {
                    if (!"PENDING".equals(r.getStatus())) {
                        throw new ConflictException("Request must have status PENDING");
                    }
                    r.setStatus("REJECTED");
                    requestRepository.save(r);
                    rejectedList.add(r);
                }
                break;

            default:
                throw new ConflictException("Invalid status: " + newStatus);
        }

        // Переводим в DTO
        List<ParticipationRequestDto> confirmedDto = confirmedList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedDto = rejectedList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedDto, rejectedDto);
    }
}
