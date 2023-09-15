package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("select new ru.practicum.dto.stats.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getStats(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select new ru.practicum.dto.stats.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ViewStats> getStatsUnique(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select new ru.practicum.dto.stats.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "and s.uri IN (?3) " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getStatsWithUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("select new ru.practicum.dto.stats.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "and s.uri IN (?3) " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ViewStats> getStatsWithUriUnique(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);
}
