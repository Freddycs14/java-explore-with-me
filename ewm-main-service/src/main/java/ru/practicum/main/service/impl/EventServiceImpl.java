package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.*;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventShort;
import ru.practicum.main.model.enums.EventState;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.EventService;
import ru.practicum.main.service.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatService statService;

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublishedEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не опубликовано");
        }
        Map<Long, Long> view = statService.toView(List.of(event));
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(List.of(event));
        statService.addHits(request);
        event.setViews(view.getOrDefault(eventId, 0L));
        event.setConfirmedRequests(confirmedRequest.getOrDefault(event.getId(), 0L));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                  Integer size, HttpServletRequest request) {
        if (sort != null && sort.equals("EVENT_DATE")) {
            sort = "eventDate";
        } else {
            sort = "id";
        }
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = now;
            rangeEnd = rangeStart.plusYears(1000);
        }

        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала сортировки должна быть ранне конца сортировки");
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sort).descending());
        List<Event> list = eventRepository.findAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        Map<Long, Long> view = statService.toView(list);
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(list);
        List<EventShort> listShort = new ArrayList<>();
        list.forEach(event -> listShort.add(EventMapper.toEventShort(event, view.getOrDefault(event.getId(), 0L),
                confirmedRequest.getOrDefault(event.getId(), 0L))));

        if (sort.equals("VIEWS")) {
            listShort.sort(Comparator.comparingLong(EventShort::getViews));
        }
        statService.addHits(request);
        return EventMapper.toListEventShortDto(listShort);
    }
}
