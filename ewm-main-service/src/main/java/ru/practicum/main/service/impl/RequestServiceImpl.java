package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mappers.RequestMapper;
import ru.practicum.main.model.ConfirmedRequestShort;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Request;
import ru.practicum.main.model.User;
import ru.practicum.main.model.enums.EventState;
import ru.practicum.main.model.enums.RequestStatus;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(("Пользователь с id " + userId + " не найден")));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(("Событие не найдено или недоступно")));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Вы уже отправляли заявку на участие в данном событии");
        }
        RequestStatus status;
        if (event.getRequestModeration().equals(Boolean.FALSE) || event.getParticipantLimit().equals(0)) {
            status = RequestStatus.CONFIRMED;
        } else {
            status = RequestStatus.PENDING;
        }
        List<ConfirmedRequestShort> requestShorts = requestRepository.countByEvent(List.of(eventId));
        if (event.getParticipantLimit() > 0 && requestShorts.size() >= event.getParticipantLimit()) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now().withNano(0))
                .event(event)
                .requester(user)
                .status(status)
                .build();
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto canceledRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException("Такого пользователя не существует");
        }
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Данного запроса не существует"));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(request);
    }
}
