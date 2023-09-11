package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationShort {
    Long id;
    List<EventShort> events;
    Boolean pinned;
    String title;
}
