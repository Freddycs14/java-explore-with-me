package ru.practicum.main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestUpdate {
    List<Request> requestConformed = new ArrayList<>();
    List<Request> requestCansel = new ArrayList<>();
}
