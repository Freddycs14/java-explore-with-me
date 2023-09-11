package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShort {
    Long id;
    String annotation;
    Category category;
    Long confirmedRequests;
    String description;
    LocalDateTime eventDate;
    User initiator;
    Boolean paid;
    String title;
    Long views;
}
