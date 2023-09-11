package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.*;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.mappers.RequestMapper;
import ru.practicum.main.model.*;
import ru.practicum.main.model.enums.*;
import ru.practicum.main.repository.*;
import ru.practicum.main.service.EventPrivateService;
import ru.practicum.main.service.StatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatService statService;


    @Override
    @Transactional
    public List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(events);
        Map<Long, Long> mapView = statService.toView(events);
        events.forEach(event -> {
            event.setConfirmedRequests(confirmedRequest.getOrDefault(event.getId(), 0L));
            event.setViews(mapView.getOrDefault(event.getId(), 0L));
        });
        return EventMapper.toListEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                new NotFoundException("Категория не найдена")));
        event.setCreatedOn(LocalDateTime.now().withNano(0));
        event.setLocation(locationRepository.save(event.getLocation()));
        event.setInitiator(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")));
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено или недоступно");
        }
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId);
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(List.of(event));
        Map<Long, Long> mapView = statService.toView(List.of(event));
        event.setViews(mapView.getOrDefault(eventId, 0L));
        event.setConfirmedRequests(confirmedRequest.getOrDefault(eventId, 0L));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Ивент не найден"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Изменения может вносить только создатель события");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        LocalDateTime eventTime = updateEvent.getEventDate();
        if (eventTime != null) {
            if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время события не могут быть раньше чем за 2 часа до данного момента");
            }
            event.setEventDate(eventTime);
        }
        UpdateEventStatus status = updateEvent.getStateAction();
        if (status != null) {
            if (status.equals(UpdateEventStatus.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
            if (status.equals(UpdateEventStatus.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена")));
        }
        Map<Long, Long> view = statService.toView(List.of(event));
        Map<Long, Long> confirmedRequest = statService.toConfirmedRequest(List.of(event));
        event.setViews(view.getOrDefault(eventId, 0L));
        event.setConfirmedRequests(confirmedRequest.getOrDefault(eventId, 0L));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new ConflictException("Вы не являетесь иницатором данного события");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id нет");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("События с id " + eventId + " не существует"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Вы не являетесь инциатором события");
        }
        int confirmedRequest = statService.toConfirmedRequest(List.of(event)).values().size();

        if (event.getParticipantLimit() != 0 && confirmedRequest >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит по заявкам исчерпан");
        }
        RequestUpdate requestUpdate = new RequestUpdate();
        List<Long> requestIds = requestDto.getRequestIds();
        for (Long requestId : requestIds) {
            Request request = requestRepository.findById(requestId).orElseThrow(() ->
                    new NotFoundException("Данный запрос не найден"));
            if (requestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestUpdate.getRequestConformed().add(request);
            }
            if (requestDto.getStatus().equals(RequestStatus.REJECTED)) {
                request.setStatus(RequestStatus.REJECTED);
                requestUpdate.getRequestCansel().add(request);
            }
        }
        return RequestMapper.toEventRequestStatusUpdateResult(requestUpdate);
    }
}
