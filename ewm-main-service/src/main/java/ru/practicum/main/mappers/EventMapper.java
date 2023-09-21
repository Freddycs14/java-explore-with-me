package ru.practicum.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.*;
import ru.practicum.main.model.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event toEvent(NewEventDto eventDto) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(Category.builder().id(eventDto.getCategory()).build())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .location(eventDto.getLocation())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .title(eventDto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn().format(FORMATTER));
        }
        return eventFullDto;

    }

    public EventShortDto toEventShortDto(EventShort eventShort) {
        return EventShortDto.builder()
                .id(eventShort.getId())
                .annotation(eventShort.getAnnotation())
                .category(CategoryMapper.toCategoryDto(eventShort.getCategory()))
                .confirmedRequests(eventShort.getConfirmedRequests())
                .eventDate(eventShort.getEventDate().format(FORMATTER))
                .initiator(UserMapper.toUserShortDto(eventShort.getInitiator()))
                .paid(eventShort.getPaid())
                .title(eventShort.getTitle())
                .views(eventShort.getViews())
                .build();
    }

    public List<EventFullDto> toListEventFullDto(List<Event> list) {
        return list.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public EventShort toEventShort(Event event, Long view, Long confirmedRequests) {
        return EventShort.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .confirmedRequests(confirmedRequests)
                .views(view)
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static List<EventShortDto> toListEventShortDto(List<EventShort> list) {
        return list.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventShort toEventShort(Event event) {
        return EventShort.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static List<EventShort> toListEventShort(List<Event> list) {
        return list.stream().map(EventMapper::toEventShort).collect(Collectors.toList());
    }

    public EventCommentDto toEventCommentDto (Event event) {
        return EventCommentDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }
}
