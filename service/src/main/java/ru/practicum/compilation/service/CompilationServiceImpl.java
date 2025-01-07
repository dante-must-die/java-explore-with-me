package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        Compilation c = new Compilation();
        c.setTitle(dto.getTitle());
        c.setPinned(dto.getPinned() != null && dto.getPinned());

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findAllById(dto.getEvents())
                    .stream().collect(Collectors.toSet());
            c.setEvents(events);
        } else {
            c.setEvents(Collections.emptySet());
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(c));
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation c = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));
        if (dto.getTitle() != null) {
            c.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            c.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            Set<Event> events = eventRepository.findAllById(dto.getEvents())
                    .stream().collect(Collectors.toSet());
            c.setEvents(events);
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(c));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        List<Compilation> comps = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        if (pinned != null) {
            comps = comps.stream()
                    .filter(x -> x.isPinned() == pinned)
                    .collect(Collectors.toList());
        }
        return comps.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation c = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));
        return CompilationMapper.toCompilationDto(c);
    }
}
