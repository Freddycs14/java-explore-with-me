package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.model.enums.UpdateEventStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventUpdate {
    String annotation;
    Long category;
    String description;
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    UpdateEventStatus state;
    String title;
}
