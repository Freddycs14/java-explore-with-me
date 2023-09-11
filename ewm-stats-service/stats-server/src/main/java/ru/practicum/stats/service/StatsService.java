package ru.practicum.stats.service;

import ru.practicum.dto.stats.StatsDto;
import ru.practicum.dto.stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsDto create(StatsDto hit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
