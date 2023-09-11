package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.model.enums.StateActionAdmin;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAdmin {
    String annotation;
    Long category;
    String description;
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionAdmin state;
    String title;
}
