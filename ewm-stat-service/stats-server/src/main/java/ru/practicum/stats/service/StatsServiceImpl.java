package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.stats.StatsDto;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.model.mapper.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public StatsDto create(StatsDto hit) {
        Stats stats = StatsMapper.toStats(hit);
        Stats postedHit = statsRepository.save(stats);
        return StatsMapper.toStatsDto(postedHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> stats;
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                stats = statsRepository.getStatsUnique(start, end);
            } else {
                stats = statsRepository.getStatsWithUriUnique(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                stats = statsRepository.getStats(start, end);
            } else {
                stats = statsRepository.getStatsWithUri(start, end, uris);
            }
        }
        return stats;
    }
}
