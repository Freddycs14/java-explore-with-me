package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.model.ConfirmedRequestShort;
import ru.practicum.main.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventId(Long eventId);

    @Query("SELECT new ru.practicum.main.model.ConfirmedRequestShort(r.event.id, count(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.event.id in ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event ")
    List<ConfirmedRequestShort> countByEvent(List<Long> longs);
}
