package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable page);

    Event findByIdAndInitiator_Id(Long eventId, Long initiator);

    boolean existsByCategoryId(Long catId);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e " +
            "WHERE ((:text IS NULL) OR ((LOWER(e.annotation) LIKE CONCAT('%', LOWER(:text), '%')) OR (LOWER(e.description) LIKE CONCAT('%', LOWER(:text), '%')))) " +
            "AND (e.category.id IN :categories OR :categories IS NULL) " +
            "AND (e.paid = :paid OR :paid IS NULL) " +
            "AND (e.eventDate > :rangeStart OR :rangeStart IS NULL) AND (e.eventDate < :rangeEnd OR :rangeEnd IS NULL) " +
            "AND (:onlyAvailable = FALSE OR ((:onlyAvailable = TRUE AND e.participantLimit > (SELECT COUNT(r.id) FROM Request AS r WHERE e.id = r.event.id))) " +
            "OR (e.participantLimit > 0 )) ")
    List<Event> findAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN ?1 OR ?1 IS NULL) " +
            "AND (e.state IN ?2 OR ?2 IS NULL) " +
            "AND (e.category.id IN ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate > ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate < ?5 OR ?5 IS NULL) ")
    List<Event> getAllUsersEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);
}