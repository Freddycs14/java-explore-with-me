package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mappers.CompilationMapper;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.CompilationShort;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventShort;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CompilationService;
import ru.practicum.main.service.StatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatService statService;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events;
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            events = new ArrayList<>();
        } else {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilations(CompilationMapper.toNewCompilation(newCompilationDto), events));
        CompilationShort compilationShort = getCompilationShort(compilation);
        log.info("Создание новой подборки");
        return CompilationMapper.toCompilationDto(compilationShort);
    }

    @Override
    @Transactional
    public void removeCompilation(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена или недопступна"));
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена или недопступна"));
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null && !compilationDto.getTitle().isBlank()) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }
        CompilationShort compilationShort = getCompilationShort(compilation);
        return CompilationMapper.toCompilationDto(compilationShort);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationsList(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        log.info("Получение списка подборок");
        return CompilationMapper.toListCompilationDtoFromCompilation(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена или недопступна"));
        CompilationShort compilationShort = getCompilationShort(compilation);

        log.info("Получение подборки по id");
        return CompilationMapper.toCompilationDto(compilationShort);
    }

    private CompilationShort getCompilationShort(Compilation compilation) {
        Map<Long, Long> view = statService.toView(compilation.getEvents());
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(compilation.getEvents());
        List<EventShort> listEventShorts = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShort(event, view.getOrDefault(event.getId(), 0L),
                        confirmedRequest.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationShort(compilation, listEventShorts);
    }
}
